package org.hisp.hieboot.camel.kamelet.rapidpro;

import org.apache.camel.CamelContext;
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
public class HieSendBroadcastRapidProSinkKameletTestCase extends AbstractHieRapidProKameletTestCase {

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private ProducerTemplate producerTemplate;

    @BeforeEach
    public void beforeEach() {
        SpringCamelContext.setNoStart(false);
    }

    @Test
    public void test() throws Exception {
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:routeUnderTest")
                        .setHeader("urns", constant(List.of("tel:+250788123123", "tel:+250788123124")))
                        .setHeader("text", constant("Hello World!"))
                        .to(String.format("kamelet:hie-send-broadcast-rapidpro-sink?rapidProApiUrl=%s&rapidProApiToken=%s", RAPIDPRO_API_URL, RAPIDPRO_API_TOKEN))
                        .to("mock:verify");
            }
        });

        MockEndpoint verifyEndpoint = camelContext.getEndpoint("mock:verify", MockEndpoint.class);
        verifyEndpoint.setExpectedMessageCount(1);
        camelContext.start();

        producerTemplate.send("direct:routeUnderTest", new DefaultExchange(camelContext));

        verifyEndpoint.await(5, TimeUnit.SECONDS);

        Map body = verifyEndpoint.getReceivedExchanges().get(0).getMessage().getBody(Map.class);
        assertEquals("Hello World!", ((Map) body.get("text")).get("eng"));
        assertEquals("queued", body.get("status"));
    }

}
