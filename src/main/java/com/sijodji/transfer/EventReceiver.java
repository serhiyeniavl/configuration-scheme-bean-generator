package com.sijodji.transfer;

import com.sijodji.model.BasicEvent;

public interface EventReceiver<T extends BasicEvent> {
    void processReceivedEvent(T event);
}
