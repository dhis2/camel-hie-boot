package org.hisp.hieboot.camel.spi;

import org.apache.camel.Message;

import java.util.List;

public interface MessageRepository {
    void store(String key, Message message);

    void store(String key, Message message, String context);

    List<RepositoryMessage> retrieve(String key, int startIndex, int stopIndex);

    List<RepositoryMessage> retrieve(String key);

    List<RepositoryMessage> delete(String key);

    RepositoryMessage move(String oldKey, String newKey);
}
