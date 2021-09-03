package com.sijodji.context.bpp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sijodji.config.EventsConfiguration;
import com.sijodji.config.KafkaPropertiesConfiguration;
import com.sijodji.model.BasicEvent;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.StringUtils;

import java.beans.Introspector;
import java.util.Map;

import static com.sijodji.context.util.BeanNameUtils.getBeanName;

public class KafkaTemplateBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (beanFactory.containsBean(Introspector.decapitalize(EventsConfiguration.class.getSimpleName()))) {
            EventsConfiguration eventsConfiguration = beanFactory.getBean(EventsConfiguration.class);
            KafkaPropertiesConfiguration kafkaPropertiesConfiguration = beanFactory.getBean(KafkaPropertiesConfiguration.class);

            eventsConfiguration.getEvents().forEach((eventName, eventConfig) -> {
                if (eventConfig.isEnable() && StringUtils.hasText(eventConfig.getProducer())) {
                    Map<String, Object> producerProps = kafkaPropertiesConfiguration.getProducers().get(eventConfig.getProducer()).buildProperties();

                    ProducerFactory<String, ? extends BasicEvent> producerFactory = new DefaultKafkaProducerFactory<>(
                            producerProps, new StringSerializer(), new JsonSerializer<>(beanFactory.getBean(ObjectMapper.class)));

                    String kafkaTemplateBeanName = getBeanName(eventName, KafkaTemplate.class);
                    KafkaTemplate<String, ? extends BasicEvent> kafkaTemplate = new KafkaTemplate<>(producerFactory);
                    kafkaTemplate.setBeanName(Introspector.decapitalize(kafkaTemplateBeanName));

                    beanFactory.registerSingleton(kafkaTemplateBeanName, kafkaTemplate);
                }
            });
        }
    }
}
