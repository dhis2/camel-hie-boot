package org.hisp.hieboot.camel.impl;

import org.apache.camel.CamelContext;
import org.apache.camel.Route;
import org.apache.camel.impl.DefaultModelReifierFactory;
import org.apache.camel.model.ProcessorDefinition;
import org.hisp.hieboot.camel.reifier.HieBootRouteReifier;

public class HieBootModelReifierFactory extends DefaultModelReifierFactory {

    @Override
    public Route createRoute(CamelContext camelContext, Object routeDefinition) {
        return new HieBootRouteReifier(camelContext, (ProcessorDefinition<?>) routeDefinition).createRoute();
    }
}
