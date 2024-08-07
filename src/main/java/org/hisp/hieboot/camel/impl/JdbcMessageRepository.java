package org.hisp.hieboot.camel.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.support.DefaultMessage;
import org.apache.camel.support.MessageHelper;
import org.apache.camel.support.service.ServiceSupport;
import org.hisp.hieboot.camel.spi.MessageRepository;
import org.hisp.hieboot.camel.spi.RepositoryMessage;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.camel.builder.Builder.simple;

public class JdbcMessageRepository extends ServiceSupport implements MessageRepository {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    @Autowired
    private ProducerTemplate producerTemplate;

    @Autowired
    private CamelContext camelContext;

//    @Autowired
//    private ObjectMapper objectMapper;

    private String dataSourceName;

    public JdbcMessageRepository(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    @Override
    public List<RepositoryMessage> retrieve(String key) {
        Exchange jdbcExchange = new DefaultExchange(camelContext);
        Message jdbcMessage = jdbcExchange.getMessage();
        jdbcMessage.setBody(simple("${properties:message.store.select.{{spring.sql.init.platform}}}").evaluate(jdbcExchange, String.class));
        jdbcMessage.setHeader("key", key.replace("*", "%"));

        return doRetrieve(jdbcExchange);
    }

    @Override
    public List<RepositoryMessage> retrieve(String key, int startIndex, int stopIndex) {
        Exchange jdbcExchange = new DefaultExchange(camelContext);
        Message jdbcMessage = jdbcExchange.getMessage();
        jdbcMessage.setBody(simple("${properties:message.store.select.range.{{spring.sql.init.platform}}}").evaluate(jdbcExchange, String.class));
        jdbcMessage.setHeader("offset", startIndex);
        jdbcMessage.setHeader("limit", stopIndex - startIndex);
        jdbcMessage.setHeader("key", key.replace("*", "%"));

        return doRetrieve(jdbcExchange);
    }

    @Override
    public List<RepositoryMessage> delete(String key) {
        Exchange deleteQueryExchange = new DefaultExchange(camelContext);
        Message deleteQueryMessage = deleteQueryExchange.getMessage();
        deleteQueryMessage.setBody(simple("${properties:message.store.delete.{{spring.sql.init.platform}}}").evaluate(deleteQueryExchange, String.class));
        deleteQueryMessage.setHeader("key", key.replace("*", "%"));
        Exchange replyDeleteQueryExchange = producerTemplate.send(String.format("jdbc:%s?useHeadersAsParameters=true", dataSourceName), deleteQueryExchange);
        if (replyDeleteQueryExchange.getException() != null) {
            throw new RuntimeException(replyDeleteQueryExchange.getException());
        }

        return toRepositoryMessages(replyDeleteQueryExchange.getMessage().getBody(List.class));
    }

    @Override
    public RepositoryMessage move(String oldKey, String newKey) {
        Exchange jdbcExchange = new DefaultExchange(camelContext);
        Message jdbcMessage = jdbcExchange.getMessage();
        jdbcMessage.setHeader("oldKey", oldKey);
        jdbcMessage.setHeader("newKey", newKey);
        jdbcMessage.setBody(simple("${properties:message.store.move.{{spring.sql.init.platform}}}").evaluate(jdbcExchange, String.class));
        Exchange replyMoveQueryExchange = producerTemplate.send(String.format("jdbc:%s?useHeadersAsParameters=true", dataSourceName), jdbcExchange);

        if (replyMoveQueryExchange.getException() != null) {
            throw new RuntimeException(replyMoveQueryExchange.getException());
        }

        List<RepositoryMessage> repositoryMessages = toRepositoryMessages(replyMoveQueryExchange.getMessage().getBody(List.class));

        if (repositoryMessages.isEmpty()) {
            return null;
        } else {
            return repositoryMessages.get(0);
        }
    }

    protected List<RepositoryMessage> toRepositoryMessages(List<Map<String, Object>> rows) {
        List<RepositoryMessage> repositoryMessages = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Message message = new DefaultMessage(camelContext);
            message.setBody(row.get("body"));

            byte[] headersAsBytes = (byte[]) row.get("headers");
            Map<String, Object> headersAsMap;
            try {
                headersAsMap = OBJECT_MAPPER.readValue(headersAsBytes, Map.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            message.setHeaders(headersAsMap);
            repositoryMessages.add(new DefaultRepositoryMessage((String) row.get("key_"), message, (String) row.get("context")));
        }

        return repositoryMessages;
    }

    @Override
    public void store(String key, Message message) {
        store(key, message, null);
    }

    @Override
    public void store(String key, Message message, String context) {
        Exchange jdbcExchange = new DefaultExchange(camelContext);
        Message jdbcMessage = jdbcExchange.getMessage();
        jdbcMessage.setHeader("key", key);
        jdbcMessage.setHeader("context", context);
        try {
            jdbcMessage.setHeader("headers", OBJECT_MAPPER.writeValueAsString(message.getHeaders()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        jdbcMessage.setHeader("body", message.getBody() != null ? MessageHelper.extractBodyAsString(message) : null);
        jdbcMessage.setBody(simple("${properties:message.store.insert.{{spring.sql.init.platform}}}").evaluate(jdbcExchange, String.class));
        Exchange replyJdbcExchange = producerTemplate.send("jdbc:" + dataSourceName + "?useHeadersAsParameters=true", jdbcExchange);
        if (replyJdbcExchange.getException() != null) {
            throw new RuntimeException(replyJdbcExchange.getException());
        }
    }

    protected List<RepositoryMessage> doRetrieve(Exchange jdbcExchange) {
        Exchange replyJdbcExchange = producerTemplate.send(String.format("jdbc:%s?useHeadersAsParameters=true", dataSourceName), jdbcExchange);
        if (replyJdbcExchange.getException() != null) {
            throw new RuntimeException(replyJdbcExchange.getException());
        }
        return toRepositoryMessages(replyJdbcExchange.getMessage().getBody(List.class));
    }
}
