package org.hisp.hieboot.camel.impl;

import org.apache.camel.CamelContext;
import org.apache.camel.Producer;
import org.apache.camel.Route;
import org.apache.camel.StartupListener;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.support.RoutePolicySupport;
import org.hisp.hieboot.camel.HieExchange;

public class ReplayableRoutePolicy extends RoutePolicySupport {

    private String replayEndpointUri;

    @Override
    public void onStart(Route route) {
        try {
            route.getCamelContext().addStartupListener(new StartupListener() {
                @Override
                public void onCamelContextStarted(CamelContext context, boolean alreadyStarted) throws Exception {

                }

                @Override
                public void onCamelContextFullyStarted(CamelContext context, boolean alreadyStarted) throws Exception {
                    Producer producer = route.getEndpoint().createProducer();
                    context.addRoutes(new RouteBuilder() {
                        @Override
                        public void configure() {
                            from(replayEndpointUri)
                                    .log("Replaying message [${headers.CamelHieReplayCheckpointMessageId}] in route [${headers.CamelHieReplayCheckpointRouteId}]")
                                    .process(exchange -> exchange.getExchangeExtension().setFromRouteId((String) exchange.getMessage().getHeader(HieExchange.REPLAY_CHECKPOINT_ROUTE_ID)))
                                    .setProperty(HieExchange.REPLAY_CHECKPOINT_MESSAGE_ID, simple(String.format("${headers.%s}", HieExchange.REPLAY_CHECKPOINT_MESSAGE_ID)))
                                    .removeHeader(HieExchange.REPLAY_CHECKPOINT_MESSAGE_ID)
                                    .setProperty(HieExchange.REPLAY_CHECKPOINT_ROUTE_ID, simple(String.format("${headers.%s}", HieExchange.REPLAY_CHECKPOINT_ROUTE_ID)))
                                    .removeHeader(HieExchange.REPLAY_CHECKPOINT_ROUTE_ID)
                                    .process(producer);
                        }
                    });
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getReplayEndpointUri() {
        return replayEndpointUri;
    }

    public void setReplayEndpointUri(String replayEndpointUri) {
        this.replayEndpointUri = replayEndpointUri;
    }
}
