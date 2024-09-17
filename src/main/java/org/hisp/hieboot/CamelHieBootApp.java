package org.hisp.hieboot;

import org.hisp.hieboot.camel.security.SelfSignedHttpClientConfigurer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public abstract class CamelHieBootApp extends SpringBootServletInitializer {

    @Bean
    public SelfSignedHttpClientConfigurer selfSignedHttpClientConfigurer() {
        return new SelfSignedHttpClientConfigurer();
    }
}
