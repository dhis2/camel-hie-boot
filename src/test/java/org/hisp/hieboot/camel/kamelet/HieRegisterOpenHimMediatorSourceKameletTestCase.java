package org.hisp.hieboot.camel.kamelet;

import io.restassured.specification.RequestSpecification;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;

import java.io.File;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@CamelSpringBootTest
@UseAdviceWith
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class HieRegisterOpenHimMediatorSourceKameletTestCase {

    @Autowired
    private CamelContext camelContext;

    @Container
    private static final DockerComposeContainer openHimDockerComposeContainer =
            new DockerComposeContainer<>(new File("src/test/resources/openhim-docker-compose.yaml")).
                    withExposedService("openhim-core", 8080);

    private Integer openHimCoreApiPortNo;
    private RequestSpecification openHimCoreRequestSpec;

    static {
        openHimDockerComposeContainer.start();
    }

    @BeforeEach
    public void beforeEach() {
        openHimCoreApiPortNo = openHimDockerComposeContainer.getServicePort("openhim-core", 8080);
        openHimCoreRequestSpec = given().auth().preemptive().basic("root@openhim.org", "openhim-password").baseUri(String.format("https://localhost:" + openHimCoreApiPortNo)).relaxedHTTPSValidation();
    }

    @Test
    public void test() throws Exception {
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from(String.format("kamelet:hie-register-openhim-mediator-source?openHimUrl=https://localhost:%s&openHimUsername=root@openhim.org&openHimPassword=openhim-password&httpClientConfigurer=#selfSignedHttpClientConfigurer",
                        openHimCoreApiPortNo)).to("mock:verify");
            }
        });

        MockEndpoint endpoint = camelContext.getEndpoint("mock:verify", MockEndpoint.class);
        endpoint.setExpectedCount(1);

        camelContext.start();

        endpoint.await(5, TimeUnit.SECONDS);
        assertEquals(1, endpoint.getReceivedCounter());
        given(openHimCoreRequestSpec).get("/mediators/urn:mediator:camel-hie-mediator").then().statusCode(200);
    }
}
