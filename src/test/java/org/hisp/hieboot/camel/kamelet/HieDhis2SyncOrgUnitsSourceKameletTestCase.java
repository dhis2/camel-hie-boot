package org.hisp.hieboot.camel.kamelet;

import io.restassured.specification.RequestSpecification;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.hisp.dhis.api.model.v40_2_2.OrganisationUnit;
import org.hisp.dhis.integration.sdk.Dhis2ClientBuilder;
import org.hisp.dhis.integration.sdk.api.Dhis2Client;
import org.hisp.hieboot.CamelHieBootApp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.junit.jupiter.Container;

import java.io.File;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = CamelHieBootApp.class)
@CamelSpringBootTest
@UseAdviceWith
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class HieDhis2SyncOrgUnitsSourceKameletTestCase {

    @Autowired
    private CamelContext camelContext;

    @Container
    private static final DockerComposeContainer dhis2DockerComposeContainer =
            new DockerComposeContainer<>(new File("src/test/resources/dhis2-docker-compose.yaml")).withExposedService("db", 5432).withExposedService("dhis2", 8080).waitingFor("dhis2", new HttpWaitStrategy().forPort(8080).forPath("/dhis-web-commons/security/login.action").forStatusCode(200));

    private Integer dbPortNo;
    private Integer dhis2PortNo;
    private RequestSpecification openHimCoreRequestSpec;

    static {
        dhis2DockerComposeContainer.start();
    }

    private Dhis2Client dhis2Client;

    @BeforeEach
    public void beforeEach() {
        dbPortNo = dhis2DockerComposeContainer.getServicePort("db", 5432);
        dhis2Client = Dhis2ClientBuilder.newClient(String.format("http://%s:%s/api", dhis2DockerComposeContainer.getServiceHost("dhis2", 8080), dhis2DockerComposeContainer.getServicePort("dhis2", 8080)), "admin", "district")
                .withCallTimeout( 500000L, TimeUnit.MILLISECONDS )
                .withWriteTimeout(500000L, TimeUnit.MILLISECONDS)
                .withReadTimeout(500000L, TimeUnit.MILLISECONDS)
                .build();
    }

    @Test
    public void test() throws Exception {
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from(String.format("kamelet:hie-dhis2-sync-org-units-source?dhis2DatabaseHostname=localhost&dhis2DatabasePort=%s&dhis2DatabaseDbname=dhis2&dhis2DatabaseUser=dhis&dhis2DatabasePassword=dhis&sourceDhis2ApiUrl=%s", dbPortNo, dhis2Client.getApiUrl()))
                        .to("mock:verify");
            }
        });

        MockEndpoint verifyEndpoint = camelContext.getEndpoint("mock:verify", MockEndpoint.class);
        verifyEndpoint.setExpectedCount(1);

        camelContext.start();

        dhis2Client.post( "organisationUnits" ).withResource(
                new OrganisationUnit().withName( "Acme" ).withCode( "ACME" ).withShortName( "Acme" )
                        .withOpeningDate( new Date( 964866178L ) ) ).transfer().close();

        verifyEndpoint.await(5000, TimeUnit.MILLISECONDS);
        assertEquals(1, verifyEndpoint.getReceivedCounter());
    }
}
