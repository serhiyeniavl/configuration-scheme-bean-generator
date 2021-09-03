package com.sijodji.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.CountDownLatch;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventConsumedMarker {

    private final CountDownLatch latch = new CountDownLatch(1);

    private Object payload;

    public boolean wasReceived() {
        return latch.getCount() == 0;
    }
}
