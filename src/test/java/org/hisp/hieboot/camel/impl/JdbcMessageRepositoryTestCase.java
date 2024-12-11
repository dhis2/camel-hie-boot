package org.hisp.hieboot.camel.impl;

import org.apache.camel.impl.DefaultCamelContext;
import org.hisp.hieboot.camel.RuntimeCamelHieBootException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JdbcMessageRepositoryTestCase {

    @Test
    public void testToRepositoryMessagesGivenUnsupportedHeadersDataType() {
        JdbcMessageRepository jdbcMessageRepository = new JdbcMessageRepository();
        jdbcMessageRepository.setCamelContext(new DefaultCamelContext());
        RuntimeCamelHieBootException runtimeCamelHieBootException = assertThrows(RuntimeCamelHieBootException.class, () -> jdbcMessageRepository.toRepositoryMessages(List.of(Map.of("headers", 1))));
        assertEquals("Unsupported data type when attempting to unmarshal Camel message headers from database: [java.lang.Integer]. Hint: are you sure you are using a supported database?", runtimeCamelHieBootException.getMessage());
    }
}
