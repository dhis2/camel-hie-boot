package org.hisp.hieboot.camel.kamelet.rapidpro;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.hisp.hieboot.CamelHieBootApp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = CamelHieBootApp.class)
@CamelSpringBootTest
@UseAdviceWith
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class HieRapidProCreateOrUpdateContactSinkKameletTestCase extends AbstractHieRapidProKameletTestCase {

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private ProducerTemplate producerTemplate;

    @Test
    public void test() throws Exception {
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:routeUnderTest")
                        .setHeader("contactName", constant("Alice"))
                        .setHeader("phoneNumber", constant("0035621000000"))
                        .to(String.format("kamelet:hie-rapidpro-create-or-update-contact-sink?rapidProApiUrl=%s&rapidProApiToken=%s", RAPIDPRO_API_URL, RAPIDPRO_API_TOKEN))
                        .to("mock:verify");
            }
        });

        camelContext.start();
        producerTemplate.send("direct:routeUnderTest", new DefaultExchange(camelContext));
    }
}
