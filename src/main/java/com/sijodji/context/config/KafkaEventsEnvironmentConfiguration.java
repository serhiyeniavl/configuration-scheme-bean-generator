package com.sijodji.context.config;

import com.sijodji.context.bpp.KafkaMessageListenerBeanPostProcessor;
import com.sijodji.context.bpp.KafkaTemplateBeanFactoryPostProcessor;
import com.sijodji.context.registry.EventSenderBeanDefinitionRegistry;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * @see com.sijodji.context.annotation.EnableKafkaEvents
 */
public class KafkaEventsEnvironmentConfiguration {

    @Bean
    BeanFactoryPostProcessor kafkaTemplateBFPP() {
        return new KafkaTemplateBeanFactoryPostProcessor();
    }

    @Bean
    BeanPostProcessor consumerBPP(ApplicationContext ac, ConfigurableBeanFactory cbf) {
        return new KafkaMessageListenerBeanPostProcessor(ac, cbf);
    }

    @Bean
    BeanDefinitionRegistryPostProcessor eventSenderBeanDefinitionRegistry() {
        return new EventSenderBeanDefinitionRegistry();
    }
}
