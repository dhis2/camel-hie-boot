package org.hisp.hieboot.camel.kamelet.rapidpro;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.ContainerState;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractHieRapidProKameletTestCase {

    protected String RAPIDPRO_API_URL;
    protected RequestSpecification RAPIDPRO_REQUEST_SPEC;
    protected String RAPIDPRO_API_TOKEN;
    protected RequestSpecification RAPIDPRO_API_REQUEST_SPEC;

    @Container
    private final DockerComposeContainer rapidProDockerComposeContainer =
            new DockerComposeContainer<>(new File("src/test/resources/rapidpro-docker-compose.yaml"))
                    .withExposedService("rapidpro", 8000).withRemoveImages(DockerComposeContainer.RemoveImages.LOCAL);

    @BeforeAll
    public void beforeAll()
            throws
            IOException,
            InterruptedException {

        rapidProDockerComposeContainer.start();
        ContainerState rapidpro = (ContainerState) rapidProDockerComposeContainer.getContainerByServiceName("rapidpro").get();

        rapidpro.execInContainer(
                "sh", "-c", "python manage.py createsuperuser --username root --email admin@dhis2.org --noinput");

        String rapidProBaseUri = String.format("http://%s:%s", rapidpro.getHost(),
                rapidpro.getFirstMappedPort());

        RAPIDPRO_API_URL = rapidProBaseUri + "/api/v2";

        RAPIDPRO_REQUEST_SPEC = new RequestSpecBuilder().setBaseUri(rapidProBaseUri).build();

        given(RAPIDPRO_REQUEST_SPEC).contentType(ContentType.URLENC).formParams(
                        Map.of("first_name", "Alice", "last_name", "Wonderland", "email", "claude@dhis2.org", "password",
                                "12345678", "timezone", "Europe/Berlin", "name", "dhis2"))
                .when()
                .post("/org/signup/").then().statusCode(302);

        RAPIDPRO_API_TOKEN = generateRapidProApiToken();
        RAPIDPRO_API_REQUEST_SPEC = new RequestSpecBuilder().setBaseUri(RAPIDPRO_API_URL).setContentType(ContentType.JSON)
                .addHeader("Authorization", "Token " + RAPIDPRO_API_TOKEN).build();
        System.setProperty("rapidpro.api.token", RAPIDPRO_API_TOKEN);
        setUpRapidPro();
    }

    private void setUpRapidPro() {
        given(RAPIDPRO_API_REQUEST_SPEC).with().body(Map.of("label", "foo", "value_type", "text")).
                post("/fields.json").
                then().statusCode(201);
        given(RAPIDPRO_API_REQUEST_SPEC).with().body(Map.of("label", "bar", "value_type", "text")).
                post("/fields.json").
                then().statusCode(201);
        given(RAPIDPRO_API_REQUEST_SPEC).with().body(Map.of("name", "Alice", "urns", List.of("tel:+250788123123"))).
                post("/contacts.json").
                then().statusCode(201);
        given(RAPIDPRO_API_REQUEST_SPEC).with().body(Map.of("name", "Bob", "urns", List.of("tel:+250788123124"))).
                post("/contacts.json").
                then().statusCode(201);
    }

    private String generateRapidProApiToken() {
        String sessionId = fetchRapidProSessionId("root", "12345678");

        return given(RAPIDPRO_REQUEST_SPEC)
                .cookie("sessionid", sessionId).when()
                .post("/api/apitoken/refresh/").then().statusCode(200).extract().path("token");
    }

    private String fetchRapidProSessionId(String username, String password) {
        ExtractableResponse<Response> loginPageResponse = given(RAPIDPRO_REQUEST_SPEC).when()
                .get("/users/login/").then().statusCode(200).extract();

        String csrfMiddlewareToken = loginPageResponse.htmlPath()
                .getString("html.body.div.div[3].div.div.div[1].div.div.form.input.@value");
        String csrfToken = loginPageResponse.cookie("csrftoken");

        return given(RAPIDPRO_REQUEST_SPEC).contentType(ContentType.URLENC)
                .cookie("csrftoken", csrfToken)
                .formParams(Map.of("csrfmiddlewaretoken", csrfMiddlewareToken,
                        "username", username, "password", password))
                .when()
                .post("/users/login/").then().statusCode(302).extract().cookie("sessionid");
    }

    @AfterAll
    public void afterAll() {
        rapidProDockerComposeContainer.stop();
    }
}
