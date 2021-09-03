package com.sijodji.model;

import java.util.Arrays;
import java.util.Objects;

public enum EventType {
    SOME_EVENT(SomeEvent.class), ANOTHER_EVENT(AnotherEvent.class);

    private final Class<? extends BasicEvent> aClass;

    EventType(Class<? extends BasicEvent> classType) {
        this.aClass = classType;
    }

    public Class<?> getClassType() {
        return aClass;
    }

    public static EventType valueOfByTypeName(String to) {
        return Arrays.stream(values())
                .filter(value -> Objects.equals(to, value.getClassType().getTypeName()))
                .findFirst()
                .orElseThrow(); //TODO: appropriate exception
    }
}
