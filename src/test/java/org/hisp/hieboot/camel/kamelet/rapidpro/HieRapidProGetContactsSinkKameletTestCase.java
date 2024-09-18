package org.hisp.hieboot.camel.kamelet.rapidpro;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.CamelContext;
import org.apache.camel.ExchangePattern;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.hisp.hieboot.CamelHieBootApp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = CamelHieBootApp.class)
@CamelSpringBootTest
@UseAdviceWith
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class HieRapidProGetContactsSinkKameletTestCase extends AbstractHieRapidProKameletTestCase {

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private ProducerTemplate producerTemplate;

    @BeforeEach
    public void beforeEach() {
        SpringCamelContext.setNoStart(false);
    }

    @Test
    public void testHieGetContactsRapidProSinkGivenNextPageUrl() throws Exception {
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:routeUnderTest")
                        .to(String.format("kamelet:hie-rapidpro-get-contacts-sink?rapidProApiUrl=%s&rapidProApiToken=%s", "mock:rapidpro", RAPIDPRO_API_TOKEN))
                        .split(body())
                            .to("mock:verify");
            }
        });

        MockEndpoint rapidProEndpoint = camelContext.getEndpoint("mock:rapidpro/contacts.json?httpMethod=GET&okStatusCodeRange=200-299", MockEndpoint.class);
        rapidProEndpoint.setExchangePattern(ExchangePattern.InOut);
        rapidProEndpoint.whenExchangeReceived(1, exchange -> {
            Map<String, Object> results = Map.of("next", "mock:rapidpro/contacts.json", "results", List.of(Map.of("name", "Bob")));
            exchange.getMessage().setBody(new ObjectMapper().writeValueAsString(results));
            exchange.getMessage().setHeader("CamelHttpResponseCode", 200);
        });
        rapidProEndpoint.whenExchangeReceived(2, exchange -> {
            Map<String, Object> results = Map.of("results", List.of(Map.of("name", "Alice")));
            exchange.getMessage().setBody(new ObjectMapper().writeValueAsString(results));
            exchange.getMessage().setHeader("CamelHttpResponseCode", 200);
        });

        MockEndpoint verifyEndpoint = camelContext.getEndpoint("mock:verify", MockEndpoint.class);
        verifyEndpoint.setExpectedMessageCount(2);
        camelContext.start();

        producerTemplate.send("direct:routeUnderTest", new DefaultExchange(camelContext));

        verifyEndpoint.await(5, TimeUnit.SECONDS);
        assertEquals(2, verifyEndpoint.getReceivedCounter());

        Map<String, Object> body = verifyEndpoint.getReceivedExchanges().get(0).getMessage().getBody(Map.class);
        assertEquals("Bob", body.get("name"));

        body = verifyEndpoint.getReceivedExchanges().get(1).getMessage().getBody(Map.class);
        assertEquals("Alice", body.get("name"));
    }

    @Test
    public void testHieGetContactsRapidProSink() throws Exception {
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:routeUnderTest")
                        .to(String.format("kamelet:hie-rapidpro-get-contacts-sink?rapidProApiUrl=%s&rapidProApiToken=%s", RAPIDPRO_API_URL, RAPIDPRO_API_TOKEN))
                        .split(body())
                            .to("mock:verify");
            }
        });

        MockEndpoint endpoint = camelContext.getEndpoint("mock:verify", MockEndpoint.class);
        endpoint.setExpectedMessageCount(2);

        camelContext.start();

        producerTemplate.send("direct:routeUnderTest", new DefaultExchange(camelContext));

        endpoint.await(5, TimeUnit.SECONDS);
        assertEquals(2, endpoint.getReceivedCounter());
    }

    @Test
    public void testHieGetContactsRapidProSinkByUrn() throws Exception {
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:routeUnderTest")
                        .setHeader("urn", constant("tel:+250788123123"))
                        .to(String.format("kamelet:hie-rapidpro-get-contacts-sink?rapidProApiUrl=%s&rapidProApiToken=%s", RAPIDPRO_API_URL, RAPIDPRO_API_TOKEN))
                        .split(body())
                            .to("mock:verify");
            }
        });

        MockEndpoint endpoint = camelContext.getEndpoint("mock:verify", MockEndpoint.class);

        camelContext.start();

        producerTemplate.send("direct:routeUnderTest", new DefaultExchange(camelContext));

        endpoint.await(5, TimeUnit.SECONDS);
        assertEquals(1, endpoint.getReceivedCounter());
        Map<String, Object> body = endpoint.getReceivedExchanges().get(0).getMessage().getBody(Map.class);
        assertEquals("Alice", body.get("name"));
    }

}
