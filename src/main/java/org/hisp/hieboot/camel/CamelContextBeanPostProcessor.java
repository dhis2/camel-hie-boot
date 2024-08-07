package org.hisp.hieboot.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.spring.spi.SpringInjector;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ConfigurableApplicationContext;

//SEE: https://issues.apache.org/jira/browse/CAMEL-21034
public class CamelContextBeanPostProcessor implements BeanPostProcessor {

    private final ConfigurableApplicationContext applicationContext;

    public CamelContextBeanPostProcessor(ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof CamelContext) {
            ((CamelContext) bean).setInjector(new SpringInjector(applicationContext));
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
