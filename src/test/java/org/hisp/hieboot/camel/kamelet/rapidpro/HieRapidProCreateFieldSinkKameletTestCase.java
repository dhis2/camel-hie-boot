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

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.oneOf;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = CamelHieBootApp.class)
@CamelSpringBootTest
@UseAdviceWith
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class HieRapidProCreateFieldSinkKameletTestCase extends AbstractHieRapidProKameletTestCase {

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
                        .setHeader("label", constant("foo"))
                        .setHeader("type", constant("text"))
                        .to(String.format("kamelet:hie-rapidpro-create-field-sink?rapidProApiUrl=%s&rapidProApiToken=%s", RAPIDPRO_API_URL, RAPIDPRO_API_TOKEN));
            }
        });

        camelContext.start();
        producerTemplate.send("direct:routeUnderTest", new DefaultExchange(camelContext));
        given(RAPIDPRO_API_REQUEST_SPEC).get("/fields.json").then()
                .body( "results.size()", equalTo( 2 ) ).body("results[0].key", is(oneOf("bar", "foo")));
    }
}
