package org.hisp.hieboot.camel.processor.replay;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.support.DefaultMessage;
import org.hisp.hieboot.camel.spi.MessageRepository;
import org.hisp.hieboot.camel.spi.RepositoryMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

@Component
public class ReplayProcessor implements Processor {

    @Autowired
    private MessageRepository messageRepository;

    public class RepositoryMessageIterable implements Iterable<RepositoryMessage> {
        private final MessageRepository messageRepository;
        private final Exchange exchange;
        private List<RepositoryMessage> repositoryMessages;

        public RepositoryMessageIterable(MessageRepository messageRepository, Exchange exchange) {
            this.repositoryMessages = new ArrayList<>();
            this.messageRepository = messageRepository;
            this.exchange = exchange;
        }

        @Override
        public Iterator<RepositoryMessage> iterator() {
            return new Iterator<>() {
                private int currentIndex = 0;
                private int startIndex = 0;

                @Override
                public boolean hasNext() {
                    if (currentIndex >= repositoryMessages.size()) {
                        repositoryMessages.clear();
                        repositoryMessages = fetch();
                        startIndex = startIndex + 100;
                        return !repositoryMessages.isEmpty();
                    } else {
                        return true;
                    }
                }

                private List<RepositoryMessage> fetch() {
                    List<RepositoryMessage> replayRepoMessages = messageRepository.retrieve("replay:*", startIndex, startIndex + 100);
                    if (replayRepoMessages.isEmpty()) {
                        return List.of();
                    } else {
                        List<RepositoryMessage> replayingRepoMessages = new ArrayList<>();
                        for (RepositoryMessage replayRepoMessage : replayRepoMessages) {
                            RepositoryMessage replayingRepoMessage = messageRepository.move(replayRepoMessage.getKey(), replayRepoMessage.getKey().replace("replay:", "replaying:"));
                            if (replayingRepoMessage != null) {
                                replayingRepoMessages.add(replayingRepoMessage);
                            }
                        }
                        return replayingRepoMessages.isEmpty() ? fetch() : replayingRepoMessages;
                    }
                }

                @Override
                public RepositoryMessage next() {
                    RepositoryMessage repositoryMessage;
                    if (hasNext()) {
                        repositoryMessage = repositoryMessages.get(currentIndex);
                        currentIndex++;

                        Exchange copy = exchange.copy();
                        Message message = new DefaultMessage(copy);
                        message.setBody(repositoryMessage.getMessage().getBody());
                        message.setHeaders(repositoryMessage.getMessage().getHeaders());

                        return repositoryMessage;
                    } else {
                        throw new NoSuchElementException();
                    }
                }
            };
        }
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        exchange.getMessage().setBody(new RepositoryMessageIterable(messageRepository, exchange));
    }

    public MessageRepository getMessageRepository() {
        return messageRepository;
    }

    public void setMessageRepository(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }
}
