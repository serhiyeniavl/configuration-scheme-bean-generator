package com.sijodji.context.registry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sijodji.config.EventsConfiguration;
import com.sijodji.context.util.BeanNameUtils;
import com.sijodji.model.EventType;
import com.sijodji.transfer.BasicEventKafkaSender;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.ResolvableType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.StringUtils;

import java.beans.Introspector;

public class EventSenderBeanDefinitionRegistry implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws
            BeansException {
        if (beanFactory.containsBean(Introspector.decapitalize(EventsConfiguration.class.getSimpleName()))) {
            EventsConfiguration eventsConfiguration = beanFactory.getBean(EventsConfiguration.class);
            eventsConfiguration.getEvents().forEach((eventName, eventConfig) -> {
                if (StringUtils.hasText(eventConfig.getProducer())) {
                    DefaultListableBeanFactory registry = (DefaultListableBeanFactory) beanFactory;
                    String suitableKafkaTemplate = BeanNameUtils.getBeanName(eventName, KafkaTemplate.class);
                    EventType eventType = EventType.valueOf(BeanNameUtils.toUpperUnderscoreFormat(eventName));

                    ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
                    constructorArgumentValues.addIndexedArgumentValue(0, new RuntimeBeanReference(suitableKafkaTemplate));
                    constructorArgumentValues.addIndexedArgumentValue(1, new RuntimeBeanReference(ObjectMapper.class));

                    RootBeanDefinition basicEventSenderDefinition = new RootBeanDefinition(BasicEventKafkaSender.class);
                    basicEventSenderDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);
                    basicEventSenderDefinition.setConstructorArgumentValues(constructorArgumentValues);
                    basicEventSenderDefinition.setDependsOn(suitableKafkaTemplate);
                    basicEventSenderDefinition.setTargetType(ResolvableType.forClassWithGenerics(BasicEventKafkaSender.class, eventType.getClassType()));
                    basicEventSenderDefinition.setAutowireCandidate(true);
                    basicEventSenderDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);

                    registry.registerBeanDefinition(BeanNameUtils.getBeanName(eventName, BasicEventKafkaSender.class), basicEventSenderDefinition);
                }
            });
        }
    }
}
