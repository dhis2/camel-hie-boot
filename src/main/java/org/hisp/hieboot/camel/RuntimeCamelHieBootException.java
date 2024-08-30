package org.hisp.hieboot.camel;

import org.apache.camel.RuntimeCamelException;

public class RuntimeCamelHieBootException extends RuntimeCamelException {

    public RuntimeCamelHieBootException() {
        super();
    }

    public RuntimeCamelHieBootException(String message) {
        super(message);
    }

    public RuntimeCamelHieBootException(String message, Throwable cause) {
        super(message, cause);
    }

    public RuntimeCamelHieBootException(Throwable cause) {
        super(cause);
    }
}
