package org.hisp.hieboot.camel.processor.replay;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.regexp.RE;
import org.hisp.hieboot.camel.HieExchange;
import org.hisp.hieboot.camel.spi.RepositoryMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnpackRepoMessageProcessor implements Processor {
    private static final Pattern KEY_PATTERN =  Pattern.compile("([^:]+):([^:]+):([^:]+):\\[(.+)\\]");

    private static final int STATUS_INDEX = 1;
    private static final int MESSAGE_ID_INDEX = 2;
    private static final int ROUTE_ID_INDEX = 3;
    private static final int REPLAY_ENDPOINT_URI = 4;

    @Override
    public void process(Exchange exchange) throws Exception {
        RepositoryMessage repositoryMessage = exchange.getMessage().getBody(RepositoryMessage.class);
        String key = repositoryMessage.getKey();
        Matcher matcher = KEY_PATTERN.matcher(key);
        matcher.matches();
        String messageId = matcher.group(MESSAGE_ID_INDEX);
        String routeId = matcher.group(ROUTE_ID_INDEX);
        String destination = matcher.group(REPLAY_ENDPOINT_URI);

        exchange.setProperty("replayEndpointUri", destination);

        Message message = exchange.getMessage();
        message.setBody(repositoryMessage.getMessage().getBody());
        message.setHeaders(repositoryMessage.getMessage().getHeaders());
        message.setHeader(HieExchange.REPLAY_CHECKPOINT_MESSAGE_ID, messageId);
        message.setHeader(HieExchange.REPLAY_CHECKPOINT_ROUTE_ID, routeId);
    }
}
