package org.hisp.hieboot.camel.processor.rapidpro;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class SetRapidProHeadersProcessor implements Processor {

    private String rapidProApiToken;

    @Override
    public void process(Exchange exchange) throws Exception {
        exchange.getMessage().setHeader("Authorization", "Token " +  rapidProApiToken);
        exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, "application/json");
    }

    public String getRapidProApiToken() {
        return rapidProApiToken;
    }

    public void setRapidProApiToken(String rapidProApiToken) {
        this.rapidProApiToken = rapidProApiToken;
    }
}
