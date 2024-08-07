package org.hisp.hieboot.camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Route;

import java.util.Map;

public class RoutePropsToExchangePropsProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        Route route = exchange.getContext().getRoute(exchange.getFromRouteId());
        for (Map.Entry<String, Object> routeProperty : route.getProperties().entrySet()) {
            exchange.setProperty(routeProperty.getKey(), routeProperty.getValue());
        }
    }
}
