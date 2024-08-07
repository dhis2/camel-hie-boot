package org.hisp.hieboot.camel;

import org.hisp.hieboot.camel.spi.MessageRepository;
import org.hisp.hieboot.camel.impl.JdbcMessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

abstract class MessageRepositoryConfiguration {

    private MessageRepository messageRepository;

    @Configuration
    @ConditionalOnMissingBean(MessageRepository.class)
    @ConditionalOnProperty(name = "camel.messageRepository.className", havingValue = "org.hisp.hieboot.camel.impl.JdbcMessageRepository",
            matchIfMissing = true)
    static class Jdbc {

        @Value("${camel.messageRepository.datasourceName}")
        private String datasourceName;

        @Bean
        public MessageRepository jdbcMessageRepository() {
            return new JdbcMessageRepository(datasourceName);
        }
    }

}
