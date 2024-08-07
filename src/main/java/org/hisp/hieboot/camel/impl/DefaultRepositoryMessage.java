package org.hisp.hieboot.camel.impl;

import org.apache.camel.Message;
import org.hisp.hieboot.camel.spi.RepositoryMessage;

public class DefaultRepositoryMessage implements RepositoryMessage {

    private final String context;
    private final String key;
    private final Message message;

    public DefaultRepositoryMessage(String key, Message message, String context) {
        this.key = key;
        this.message = message;
        this.context = context;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public Message getMessage() {
        return message;
    }

    @Override
    public String getContext() {
        return "";
    }
}
