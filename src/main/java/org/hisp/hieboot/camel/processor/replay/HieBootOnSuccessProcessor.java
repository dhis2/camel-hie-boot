package org.hisp.hieboot.camel.processor.replay;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.hisp.hieboot.camel.HieExchange;
import org.hisp.hieboot.camel.spi.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class HieBootOnSuccessProcessor implements Processor {

    @Autowired
    private MessageRepository messageRepository;

    @Override
    public void process(Exchange exchange) throws Exception {
        String replayCheckpointMessageId = (String) exchange.getProperty(HieExchange.REPLAY_CHECKPOINT_MESSAGE_ID);
        if (replayCheckpointMessageId != null) {
            messageRepository.delete(String.format("processing:%s:%s:*", replayCheckpointMessageId, exchange.getFromRouteId()));
        }
    }
}
