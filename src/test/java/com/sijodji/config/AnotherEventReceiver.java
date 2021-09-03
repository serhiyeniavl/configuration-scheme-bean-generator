package com.sijodji.config;

import com.sijodji.model.AnotherEvent;
import com.sijodji.transfer.EventReceiver;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class AnotherEventReceiver implements EventReceiver<AnotherEvent> {

    @Getter
    private EventConsumedMarker eventConsumedMarker;

    @PostConstruct
    void init() {
        eventConsumedMarker = new EventConsumedMarker();
    }

    @Override
    public void processReceivedEvent(AnotherEvent event) {
        log.info("AnotherEventReceiver: " + event);
        eventConsumedMarker.getLatch().countDown();
        eventConsumedMarker.setPayload(event);
    }
}
