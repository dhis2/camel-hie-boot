package org.hisp.hieboot;

import org.apache.camel.CamelContext;
import org.hisp.hieboot.camel.security.SelfSignedHttpClientConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
public abstract class CamelHieBootApp extends SpringBootServletInitializer {

    protected static final Logger LOGGER = LoggerFactory.getLogger(CamelHieBootApp.class);

    @Value("${server.ssl.enabled:false}")
    protected Boolean serverSslEnabled;

    @Value("${server.servlet.context-path:}")
    protected String serverServletContextPath;

    @Value("${management.endpoints.web.base-path}")
    protected String managementEndpointsWebBasePath;

    @Value("${server.port}")
    protected int serverPort;

    @Autowired
    protected CamelContext camelContext;

    @Bean
    public SelfSignedHttpClientConfigurer selfSignedHttpClientConfigurer() {
        return new SelfSignedHttpClientConfigurer();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReadyEvent()
            throws
            IOException {

        String baseUrl = String.format("%s://%s:%s%s", serverSslEnabled ? "https" : "http",
                InetAddress.getLocalHost().getHostAddress(), serverPort, serverServletContextPath);

        StringBuilder onlineBanner = new StringBuilder();
        onlineBanner.append("Hawtio console: ").append(baseUrl).append(managementEndpointsWebBasePath)
                .append("/hawtio\n");

        LOGGER.info(
                String.format(StreamUtils.copyToString(
                                Thread.currentThread().getContextClassLoader().getResourceAsStream("online-banner.txt"),
                                StandardCharsets.UTF_8),
                        onlineBanner));
    }
}
