package org.hisp.hieboot.camel.expression;

import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.hisp.hieboot.camel.RuntimeCamelHieBootException;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class PageExpressionIterator implements Expression {
    private static class LazyResultsIterable implements Iterable<Map<String, Object>> {
        private final Exchange exchange;
        private final List<Map<String, Object>> results;
        private final String nextPageUrl;

        public LazyResultsIterable(Exchange exchange) {
            this.exchange = exchange;
            Map<String, Object> body = exchange.getMessage().getBody(Map.class);
            results = ((List) body.get("results"));
            nextPageUrl = (String) body.get("next");
        }

        @Override
        public Iterator<Map<String, Object>> iterator() {
            return new LazyResultsIterator(results.iterator(), nextPageUrl, exchange);
        }

        public List<Map<String, Object>> getResults() {
            return results;
        }

        public String getNextPageUrl() {
            return nextPageUrl;
        }
    }

    private static class LazyResultsIterator implements Iterator<Map<String, Object>> {

        private Iterator<Map<String, Object>> results;
        private String nextPageUrl;
        private final Exchange exchange;

        public LazyResultsIterator(Iterator<Map<String, Object>> results, String nextPageUrl, Exchange exchange) {
            this.results = results;
            this.nextPageUrl = nextPageUrl;
            this.exchange = exchange;
        }

        @Override
        public boolean hasNext() {
            if (results.hasNext()) {
                return true;
            } else if (nextPageUrl != null) {
                Exchange nextPageExchange = exchange.copy();
                nextPageExchange.getMessage().setHeader("nextPageUrl", nextPageUrl);
                try {
                    exchange.getFromEndpoint().createProducer().process(nextPageExchange);
                } catch (Exception e) {
                    throw new RuntimeCamelHieBootException(e);
                }
                LazyResultsIterable resultsIterable = nextPageExchange.getMessage().getBody(LazyResultsIterable.class);
                results = resultsIterable.getResults().iterator();
                nextPageUrl = resultsIterable.getNextPageUrl();

                return hasNext();
            } else {
                return false;
            }
        }

        @Override
        public Map<String, Object> next() {
            if (hasNext()) {
                return results.next();
            } else {
                throw new NoSuchElementException();
            }
        }
    }

    @Override
    public <T> T evaluate(Exchange exchange, Class<T> type) {
        return (T) new LazyResultsIterable(exchange);
    }
}
