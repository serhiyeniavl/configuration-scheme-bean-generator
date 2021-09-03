package com.sijodji.transfer;

import com.sijodji.model.BasicEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;

@RequiredArgsConstructor
public final class EventKafkaConsumer<T extends BasicEvent> implements MessageListener<String, T> {

    private final EventReceiver<T> eventReceiver;

    @Override
    public void onMessage(ConsumerRecord<String, T> data) {
        eventReceiver.processReceivedEvent(data.value());
    }
}
