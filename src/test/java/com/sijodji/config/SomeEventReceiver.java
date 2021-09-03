package com.sijodji.config;

import com.sijodji.model.SomeEvent;
import com.sijodji.transfer.EventReceiver;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class SomeEventReceiver implements EventReceiver<SomeEvent> {

    @Getter
    private EventConsumedMarker eventConsumedMarker;

    @PostConstruct
    void init() {
        eventConsumedMarker = new EventConsumedMarker();
    }

    @Override
    public void processReceivedEvent(SomeEvent event) {
        log.info("SomeEventReceiver: " + event);
        eventConsumedMarker.getLatch().countDown();
        eventConsumedMarker.setPayload(event);
    }
}
