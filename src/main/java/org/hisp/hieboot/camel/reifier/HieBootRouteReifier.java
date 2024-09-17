package org.hisp.hieboot.camel.reifier;

import org.apache.camel.CamelContext;
import org.apache.camel.NamedNode;
import org.apache.camel.Processor;
import org.apache.camel.Route;
import org.apache.camel.model.KameletDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.reifier.RouteReifier;
import org.apache.camel.spi.InterceptStrategy;
import org.hisp.hieboot.camel.HieExchange;
import org.hisp.hieboot.camel.model.HieBootNoOutputDefinition;

public class HieBootRouteReifier extends RouteReifier {
    public HieBootRouteReifier(CamelContext camelContext, ProcessorDefinition<?> definition) {
        super(camelContext, definition);
    }

    @Override
    public Route createRoute() {
        if (definition.isKamelet() == null || !definition.isKamelet()) {
            if (!hasOutputs(definition.getOutputs(), true)) {
                definition.getOutputs().add(new HieBootNoOutputDefinition());
            }
        }
        definition.addInterceptStrategy((context, namedNode, target, nextTarget) -> {
            if (namedNode instanceof KameletDefinition && ((KameletDefinition) namedNode).getName().equals("hie-replay-checkpoint-action")) {
                return exchange -> {
                    exchange.setProperty(HieExchange.REPLAY_CHECKPOINT_ROUTE_ID, exchange.getUnitOfWork().getRoute().getRouteId());
                    exchange.setProperty(HieExchange.REPLAY_CHECKPOINT_MESSAGE_ID, exchange.getUnitOfWork().getOriginalInMessage().getMessageId());
                    target.process(exchange);
                };
            } else {
                return target;
            }
        });
        Route newRoute = super.createRoute();
        if (definition.getTemplateParameters() != null) {
            newRoute.getProperties().putAll(definition.getTemplateParameters());
        }
        return newRoute;
    }
}
