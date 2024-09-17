package org.hisp.hieboot.camel.processor.rapidpro;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class RemoveRapidProHeadersProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        exchange.getMessage().removeHeader("Authorization");
        exchange.getMessage().removeHeader(Exchange.CONTENT_TYPE);
    }
}
