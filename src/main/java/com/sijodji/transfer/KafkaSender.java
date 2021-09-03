package com.sijodji.transfer;


public interface KafkaSender<T> {

    default void send(T obj) {
        send(obj, "");
    }

    void send(T obj, String topic);
}
