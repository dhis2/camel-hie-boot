package org.hisp.hieboot.camel.processor.replay;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.hisp.hieboot.camel.HieExchange;
import org.hisp.hieboot.camel.spi.MessageRepository;
import org.hisp.hieboot.camel.spi.RepositoryMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

@Component
public class HieBootOnFailedProcessor implements Processor {

    @Autowired
    private MessageRepository messageRepository;

    @Override
    public void process(Exchange exchange) throws Exception {
        List<RepositoryMessage> repositoryMessages = messageRepository.retrieve(String.format("processing:%s:%s:*", exchange.getProperty(HieExchange.REPLAY_CHECKPOINT_MESSAGE_ID), exchange.getUnitOfWork().getRoute().getRouteId()));

        if (!repositoryMessages.isEmpty()) {
            RepositoryMessage repositoryMessage = repositoryMessages.get(0);
            StringWriter stackTraceStringWriter = new StringWriter();
            Exception exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
            exception.printStackTrace(new PrintWriter(stackTraceStringWriter));

            messageRepository.store(repositoryMessage.getKey().replace("processing:", "failed:"), repositoryMessage.getMessage(), stackTraceStringWriter.toString());
            messageRepository.delete(repositoryMessage.getKey());
        }
    }
}
