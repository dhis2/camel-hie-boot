package org.hisp.hieboot.camel.processor.replay;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.hisp.hieboot.camel.HieExchange;
import org.hisp.hieboot.camel.spi.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class CheckpointProcessor implements Processor {

    protected static final Logger LOGGER = LoggerFactory.getLogger(CheckpointProcessor.class);

    @Autowired
    private MessageRepository messageRepository;

    private String replayEndpointUri;

    @Override
    public void process(Exchange exchange) throws Exception {
        String messageId = (String) exchange.getProperty(HieExchange.REPLAY_CHECKPOINT_MESSAGE_ID);
        String replayableRouteId = (String) exchange.getProperty(HieExchange.REPLAY_CHECKPOINT_ROUTE_ID);

        messageRepository.store(String.format("processing:%s:%s:[%s]", messageId, replayableRouteId, replayEndpointUri), exchange.getMessage());
        messageRepository.delete(String.format("replaying:%s:%s:[%s]", messageId, replayableRouteId, replayEndpointUri));
        LOGGER.info("Created checkpoint for message [{}] in route [{}]", messageId, replayableRouteId);
    }

    public MessageRepository getMessageRepository() {
        return messageRepository;
    }

    public void setMessageRepository(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public String getReplayEndpointUri() {
        return replayEndpointUri;
    }

    public void setReplayEndpointUri(String replayEndpointUri) {
        this.replayEndpointUri = replayEndpointUri;
    }


}
