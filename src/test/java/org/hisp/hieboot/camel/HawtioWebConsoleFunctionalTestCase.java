package org.hisp.hieboot.camel;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.parsing.Parser;
import io.restassured.specification.RequestSpecification;
import org.hisp.hieboot.CamelHieBootApp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = CamelHieBootApp.class)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class HawtioWebConsoleFunctionalTestCase {
    @LocalServerPort
    private int serverPort;

    private RequestSpecification hawtioRequestSpec;

    @BeforeEach
    public void beforeEach() {
        hawtioRequestSpec =
                new RequestSpecBuilder()
                        .setBaseUri(String.format("http://localhost:%s/management/hawtio/jolokia", serverPort))
                        .setRelaxedHTTPSValidation()
                        .build();
    }

    @Test
    public void testAnonymousHttpGet() {
        given(hawtioRequestSpec).get().then().statusCode(401);
    }

    @Test
    public void testAuthorisedHttpGet() {
        Map body = given(hawtioRequestSpec).auth().basic("test", "test").get().then().defaultParser(Parser.JSON).statusCode(200).extract().as(Map.class);
        assertEquals(200, body.get("status"));
    }
}
