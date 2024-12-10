package org.hisp.hieboot.camel.processor.replay;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.hisp.hieboot.camel.HieExchange;
import org.hisp.hieboot.camel.spi.MessageRepository;
import org.hisp.hieboot.camel.spi.RepositoryMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class FailCheckpointProcessor implements Processor {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FailCheckpointProcessor.class);

    @Autowired
    private MessageRepository messageRepository;

    @Override
    public void process(Exchange exchange) throws Exception {
        List<RepositoryMessage> repositoryMessages = messageRepository.retrieve(String.format("processing:%s:%s:*", exchange.getProperty(HieExchange.REPLAY_CHECKPOINT_MESSAGE_ID), exchange.getProperty(HieExchange.REPLAY_CHECKPOINT_ROUTE_ID)));
        if (!repositoryMessages.isEmpty()) {
            RepositoryMessage repositoryMessage = repositoryMessages.get(0);
            messageRepository.store(repositoryMessage.getKey().replace("processing:", "failed:"), repositoryMessage.getMessage(), exchange.getMessage().getHeader("errorMessage", String.class));
            messageRepository.delete(repositoryMessage.getKey());
            LOGGER.info("Failed replay checkpoint for message [{}] in route [{}]", exchange.getProperty(HieExchange.REPLAY_CHECKPOINT_MESSAGE_ID), exchange.getUnitOfWork().getRoute().getRouteId());
        }
    }

    public MessageRepository getMessageRepository() {
        return messageRepository;
    }

    public void setMessageRepository(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }
}
