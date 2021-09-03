package com.sijodji.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class AnotherEvent extends BasicEvent {
    private String anotherData;

    @Override
    public EventType getEventType() {
        return EventType.ANOTHER_EVENT;
    }
}
