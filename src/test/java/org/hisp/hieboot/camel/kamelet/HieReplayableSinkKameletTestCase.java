package org.hisp.hieboot.camel.kamelet;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.hisp.hieboot.camel.spi.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@CamelSpringBootTest
@UseAdviceWith
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class HieReplayableSinkKameletTestCase {

    @LocalServerPort
    private int serverPort;

    @Autowired
    private ProducerTemplate producerTemplate;

    @Autowired
    private CamelContext camelContext;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected MessageRepository messageRepository;

    @BeforeEach
    public void beforeEach() {
        jdbcTemplate.execute("TRUNCATE TABLE MESSAGE_STORE");
    }

    @Test
    public void testOnSuccess() throws Exception {
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:routeUnderTest")
                        .routeId("routeUnderTest")
                        .kamelet("hie-replay-checkpoint-sink")
                        .to("mock:verify");
            }
        });

        MockEndpoint endpoint = camelContext.getEndpoint("mock:verify", MockEndpoint.class);
        endpoint.setExpectedCount(1);

        camelContext.start();

        producerTemplate.send("direct:routeUnderTest", exchange -> {

        });
        endpoint.await(5, TimeUnit.SECONDS);
        assertEquals(1, endpoint.getReceivedCounter());
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM MESSAGE_STORE");
        assertEquals(0, rows.size());
    }

    @Test
    public void testOnFailure() throws Exception {
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:routeUnderTest")
                        .routeId("routeUnderTest")
                        .kamelet("hie-replay-checkpoint-sink")
                        .to("mock:verify")
                        .throwException(new Exception());
            }
        });

        MockEndpoint endpoint = camelContext.getEndpoint("mock:verify", MockEndpoint.class);
        endpoint.setExpectedCount(1);

        camelContext.start();

        Exchange exchange = new DefaultExchange(camelContext);
        String messageId = exchange.getMessage().getMessageId();
        producerTemplate.send("direct:routeUnderTest", exchange);

        endpoint.await(5, TimeUnit.SECONDS);
        assertEquals(1, endpoint.getReceivedCounter());
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM MESSAGE_STORE");
        assertEquals(1, rows.size());
        assertTrue(((String) rows.get(0).get("key_")).startsWith("failed:" + messageId + ":routeUnderTest:"));
        assertNotNull(rows.get(0).get("context"));
    }

    @Test
    public void testReplaying() throws Exception {
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:routeUnderTest")
                        .routeId("routeUnderTest")
                        .kamelet("hie-replay-checkpoint-sink?replayChannelName=routeUnderTest")
                        .to("mock:verify");
            }
        });

        jdbcTemplate.execute(String.format("INSERT INTO MESSAGE_STORE (key_, headers, body) VALUES ('%s', '{}' FORMAT JSON, 'a')", newKey(camelContext)));
        jdbcTemplate.execute(String.format("INSERT INTO MESSAGE_STORE (key_, headers, body) VALUES ('%s', '{}' FORMAT JSON, 'b')", newKey(camelContext)));
        jdbcTemplate.execute(String.format("INSERT INTO MESSAGE_STORE (key_, headers, body) VALUES ('%s', '{}' FORMAT JSON, 'c')", newKey(camelContext)));
        jdbcTemplate.execute(String.format("INSERT INTO MESSAGE_STORE (key_, headers, body) VALUES ('%s', '{}' FORMAT JSON, 'd')", newKey(camelContext)));
        jdbcTemplate.execute(String.format("INSERT INTO MESSAGE_STORE (key_, headers, body) VALUES ('%s', '{}' FORMAT JSON, 'e')", newKey(camelContext)));

        MockEndpoint endpoint = camelContext.getEndpoint("mock:verify", MockEndpoint.class);
        endpoint.setExpectedCount(5);

        camelContext.start();

        endpoint.await(5, TimeUnit.SECONDS);
        assertEquals(5, endpoint.getReceivedCounter());

        camelContext.stop();

        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM MESSAGE_STORE");
        assertTrue(rows.isEmpty());
    }

    private String newKey(CamelContext context) {
        return "replay:" + context.getUuidGenerator().generateExchangeUuid() + ":routeUnderTest:[jms:queue:routeUnderTest]";
    }
}
