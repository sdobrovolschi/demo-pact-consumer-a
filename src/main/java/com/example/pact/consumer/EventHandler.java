package com.example.pact.consumer;

public interface EventHandler<T extends Event> {

    void handle(T event);
}
