package com.sijodji.context.bpp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.CaseFormat;
import com.sijodji.config.EventsConfiguration;
import com.sijodji.config.KafkaPropertiesConfiguration;
import com.sijodji.model.BasicEvent;
import com.sijodji.model.Event;
import com.sijodji.model.EventType;
import com.sijodji.transfer.EventKafkaConsumer;
import com.sijodji.transfer.EventReceiver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.beans.Introspector;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

import static com.sijodji.context.util.BeanNameUtils.getBeanName;

@Slf4j
@RequiredArgsConstructor
public class KafkaMessageListenerBeanPostProcessor implements BeanPostProcessor {

    private final ApplicationContext applicationContext;
    private final ConfigurableBeanFactory beanFactory;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        if (Arrays.stream(ClassUtils.getAllInterfacesForClass(bean.getClass())).toList().contains(EventReceiver.class)) {
            Type type = ((ParameterizedType) bean.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
            EventType eventType = EventType.valueOfByTypeName(type.getTypeName());
            String eventName = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, eventType.name());

            if (beanFactory.containsBean(Introspector.decapitalize(EventsConfiguration.class.getSimpleName()))) {
                EventsConfiguration eventsConfiguration = beanFactory.getBean(EventsConfiguration.class);
                KafkaPropertiesConfiguration kafkaPropertiesConfiguration = beanFactory.getBean(KafkaPropertiesConfiguration.class);

                if (eventsConfiguration.getEvents().containsKey(eventName)) {
                    EventsConfiguration.EventConfiguration eventConfiguration = eventsConfiguration.getEvents().get(eventName);
                    if (eventConfiguration.isEnable() && StringUtils.hasText(eventConfiguration.getConsumer())) {
                        KafkaProperties.Consumer consumer = kafkaPropertiesConfiguration.getConsumers().get(eventConfiguration.getConsumer());

                        JsonDeserializer<? extends Event> eventJsonDeserializer
                                = new JsonDeserializer<>(eventType.getClassType(), applicationContext.getBean(ObjectMapper.class));
                        ErrorHandlingDeserializer eventJsonErrorHandlingDeserializerDeserializer =
                                new ErrorHandlingDeserializer(eventJsonDeserializer);
                        ErrorHandlingDeserializer stringErrorHandlingDeserializer =
                                new ErrorHandlingDeserializer(new StringDeserializer());
                        eventJsonDeserializer.setUseTypeHeaders(false);

                        EventKafkaConsumer eventKafkaConsumer = new EventKafkaConsumer<>((EventReceiver<? extends BasicEvent>) bean);

                        final DefaultKafkaConsumerFactory defaultKafkaConsumerFactory = new DefaultKafkaConsumerFactory<>(
                                consumer.buildProperties(),
                                stringErrorHandlingDeserializer,
                                eventJsonErrorHandlingDeserializerDeserializer);
                        ContainerProperties containerProperties = new ContainerProperties(eventType.name());
                        containerProperties.setMessageListener(eventKafkaConsumer);

                        ConcurrentMessageListenerContainer messageListener
                                = new ConcurrentMessageListenerContainer<>(defaultKafkaConsumerFactory, containerProperties);

                        String kafkaMessageListenerBeanName = getBeanName(eventName, KafkaMessageListenerContainer.class);
                        messageListener.setBeanName(kafkaMessageListenerBeanName);
                        beanFactory.registerSingleton(kafkaMessageListenerBeanName, messageListener);
                    }
                } else {
                    log.warn("Found consumer for event {} but kafka is disabled", eventName);
                }
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}

