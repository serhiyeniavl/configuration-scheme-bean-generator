package com.sijodji.model;

import java.util.UUID;

public interface Event {
    UUID getUuid();

    EventType getEventType();
}
