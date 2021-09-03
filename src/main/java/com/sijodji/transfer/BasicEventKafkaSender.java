package com.sijodji.transfer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sijodji.model.BasicEvent;
import org.springframework.kafka.core.KafkaTemplate;

public final class BasicEventKafkaSender<T extends BasicEvent> extends AbstractKafkaSender<T> {

    public BasicEventKafkaSender(KafkaTemplate<String, T> kafkaTemplate, ObjectMapper jsonMapper) {
        super(kafkaTemplate, jsonMapper, null);
    }

    @Override
    public void send(T obj) {
        super.send(obj, obj.getEventType().name());
    }

    @Override
    public void send(T obj, String topic) {
        throw new UnsupportedOperationException("This type of sender doesn't support custom topic name");
    }
}
