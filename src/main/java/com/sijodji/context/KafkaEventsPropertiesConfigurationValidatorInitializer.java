package com.sijodji.context;

import com.sijodji.config.EventsConfiguration;
import com.sijodji.config.KafkaPropertiesConfiguration;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.beans.Introspector;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class KafkaEventsPropertiesConfigurationValidatorInitializer
        implements ApplicationContextInitializer {


    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        Environment environment = applicationContext.getEnvironment();

        BindResult<EventsConfiguration> eventsConfiguration = Binder.get(environment)
                .bind("", EventsConfiguration.class);
        BindResult<KafkaPropertiesConfiguration> allKafkaConfigs = Binder.get(environment)
                .bind("events.configs", KafkaPropertiesConfiguration.class);

        List<String> errors = new ArrayList<>();

        eventsConfiguration.ifBound(eConfiguration -> eConfiguration.getEvents().forEach((eventName, eventConfig) -> {
            if (eventConfig.isEnable()) {
                if (StringUtils.hasText(eventConfig.getConsumer())) {
                    checkConsumerProps(allKafkaConfigs, eventConfig, eventName, errors);
                }
                if (StringUtils.hasText(eventConfig.getProducer())) {
                    checkProducerProps(allKafkaConfigs, eventConfig, eventName, errors);
                }
            }
        }));

        if (errors.isEmpty()) {
            beanFactory.registerSingleton(Introspector.decapitalize(EventsConfiguration.class.getSimpleName()), eventsConfiguration.get());
            beanFactory.registerSingleton(Introspector.decapitalize(KafkaPropertiesConfiguration.class.getSimpleName()), allKafkaConfigs.get());
        } else {
            throw new IllegalStateException("Invalid kafka events configuration setup. Errors: " + errors.stream().distinct().toList());
        }
    }

    private void checkConsumerProps(BindResult<KafkaPropertiesConfiguration> allKafkaConfigs,
                                    EventsConfiguration.EventConfiguration eventConfiguration,
                                    String eventName,
                                    List<String> addErrors) {

        if (allKafkaConfigs.isBound()) {
            KafkaPropertiesConfiguration kafkaPropertiesConfiguration = allKafkaConfigs.get();
            Map<String, KafkaProperties.Consumer> consumers = kafkaPropertiesConfiguration.getConsumers();
            if (Objects.isNull(consumers) || !consumers.containsKey(eventConfiguration.getConsumer())) {
                addErrors.add(String.format("For event %s consumer with name %s not found", eventName, eventConfiguration.getConsumer()));
            }
        } else {
            addErrors.add(String.format("Couldn't find properties events.configs.consumers to set up %s consumer", eventName));
        }
    }

    private void checkProducerProps(BindResult<KafkaPropertiesConfiguration> allKafkaConfigs,
                                    EventsConfiguration.EventConfiguration eventConfiguration,
                                    String eventName,
                                    List<String> addErrors) {

        if (allKafkaConfigs.isBound()) {
            KafkaPropertiesConfiguration kafkaPropertiesConfiguration = allKafkaConfigs.get();
            Map<String, KafkaProperties.Producer> producers = kafkaPropertiesConfiguration.getProducers();
            if (Objects.isNull(producers) || !producers.containsKey(eventConfiguration.getProducer())) {
                addErrors.add(String.format("For event %s producer with name %s not found", eventName, eventConfiguration.getProducer()));
            }
        } else {
            addErrors.add(String.format("Couldn't find properties events.configs.producers to set up %s producer", eventName));
        }
    }
}
