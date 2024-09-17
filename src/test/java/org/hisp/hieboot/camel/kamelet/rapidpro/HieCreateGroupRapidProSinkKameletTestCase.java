package org.hisp.hieboot.camel.kamelet.rapidpro;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
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

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.oneOf;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = CamelHieBootApp.class)
@CamelSpringBootTest
@UseAdviceWith
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class HieCreateGroupRapidProSinkKameletTestCase extends AbstractHieRapidProKameletTestCase {

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
                        .setHeader("name", constant("foo"))
                        .to(String.format("kamelet:hie-create-group-rapidpro-sink?rapidProApiUrl=%s&rapidProApiToken=%s", RAPIDPRO_API_URL, RAPIDPRO_API_TOKEN));
            }
        });

        camelContext.start();
        producerTemplate.send("direct:routeUnderTest", new DefaultExchange(camelContext));
        given(RAPIDPRO_API_REQUEST_SPEC).queryParam("name", "foo").get("/groups.json").then().log().body()
                .body("results.size()", equalTo(1)).body("results[0].name", equalTo("foo"));
    }
}
