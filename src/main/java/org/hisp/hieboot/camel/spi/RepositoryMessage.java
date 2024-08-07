package org.hisp.hieboot.camel.spi;

import org.apache.camel.Message;

public interface RepositoryMessage {

    String getKey();
    Message getMessage();
    String getContext();
}
