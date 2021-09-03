package com.sijodji.transfer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.StringUtils;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
@AllArgsConstructor
public abstract class AbstractKafkaSender<T> implements KafkaSender<T> {

    protected final KafkaTemplate<String, T> kafkaTemplate;
    protected final ObjectMapper objectMapper;

    private final String configuredTopicName;

    @Override
    public void send(T obj, String topic) {
        final String topicName = StringUtils.hasText(topic) ? topic : this.configuredTopicName;

        Message<T> message = MessageBuilder.withPayload(obj)
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .build();

        kafkaTemplate.send(message).addCallback(new ListenableFutureCallback<>() {
            @SneakyThrows
            @Override
            public void onFailure(Throwable ex) {
                log.info("Sending message {} to kafka failed. Exception: {}", objectMapper.writeValueAsString(message), ex);
            }

            @SneakyThrows
            @Override
            public void onSuccess(SendResult<String, T> result) {
                if (log.isDebugEnabled()) {
                    log.debug("Sent message {} to kafka", objectMapper.writeValueAsString(message));
                } else {
                    log.info("Sent message id: {} to kafka", message.getHeaders().getId());
                }
            }
        });

    }
}
