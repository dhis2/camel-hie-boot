package org.hisp.hieboot.camel.kamelet.replay;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.hisp.hieboot.CamelHieBootApp;
import org.hisp.hieboot.camel.spi.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = CamelHieBootApp.class)
@CamelSpringBootTest
@UseAdviceWith
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class HieFailReplayCheckpointActionKameletTestCase {

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
    public void testFail() throws Exception {
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:routeUnderTest")
                        .routeId("routeUnderTest")
                        .kamelet("hie-create-replay-checkpoint-action")
                        .setHeader("errorMessage", constant("Foo"))
                        .kamelet("hie-fail-replay-checkpoint-action")
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
        assertEquals("Foo", rows.get(0).get("context"));
    }
}
