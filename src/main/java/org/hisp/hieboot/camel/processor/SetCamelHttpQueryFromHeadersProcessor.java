package org.hisp.hieboot.camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

import java.util.HashSet;
import java.util.Set;

public class SetCamelHttpQueryFromHeadersProcessor implements Processor {

    private Set<String> headerNames = new HashSet<>();

    @Override
    public void process(Exchange exchange) throws Exception {
        StringBuilder queryStringBuilder = new StringBuilder();
        Message message = exchange.getMessage();
        for (String headerName : headerNames) {
            String headerValue = message.getHeader(headerName, String.class);
            if (headerValue != null) {
                queryStringBuilder.append(headerName).append("=").append(headerValue).append("&");
            }
        }
        if (!queryStringBuilder.isEmpty()) {
            message.setHeader(Exchange.HTTP_QUERY, queryStringBuilder.delete(queryStringBuilder.length() - 1, queryStringBuilder.length()).toString());
        }
    }

    public Set<String> getHeaderNames() {
        return headerNames;
    }

    public void setHeaderNames(Set<String> headerNames) {
        this.headerNames = headerNames;
    }
}
