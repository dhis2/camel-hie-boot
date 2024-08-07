package org.hisp.hieboot.camel;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration(proxyBeanMethods = false)
@PropertySource( "${sql.data-location}" )
public class CamelHieBootAutoConfiguration  {

    @Bean
    public CamelContextBeanPostProcessor camelContextBeanPostProcessor(ConfigurableApplicationContext applicationContext) {
        return new CamelContextBeanPostProcessor(applicationContext);
    }
}
