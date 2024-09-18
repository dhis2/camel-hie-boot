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

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = CamelHieBootApp.class)
@CamelSpringBootTest
@UseAdviceWith
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class HieRapidProGetGroupsRunsSinkKameletTestCase extends AbstractHieRapidProKameletTestCase {

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private ProducerTemplate producerTemplate;

    @BeforeEach
    public void beforeEach() {
        SpringCamelContext.setNoStart(false);
    }

    @Test
    public void testHieGetGroupsRapidProSink() throws Exception {
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:routeUnderTest")
                        .to(String.format("kamelet:hie-rapidpro-get-groups-sink?rapidProApiUrl=%s&rapidProApiToken=%s", RAPIDPRO_API_URL, RAPIDPRO_API_TOKEN))
                        .split(body())
                        .to("mock:verify");
            }
        });

        MockEndpoint endpoint = camelContext.getEndpoint("mock:verify", MockEndpoint.class);
        endpoint.setExpectedMessageCount(2);

        camelContext.start();

        producerTemplate.send("direct:routeUnderTest", new DefaultExchange(camelContext));

        endpoint.await(5, TimeUnit.SECONDS);
        assertEquals(3, endpoint.getReceivedCounter());
    }
}
