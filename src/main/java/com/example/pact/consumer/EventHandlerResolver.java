package com.example.pact.consumer;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;

@FunctionalInterface
public interface EventHandlerResolver {

    static EventHandlerResolver newResolver(Consumer<EventHandlerRegistry> block) {
        var eventTypeToHandler = new HashMap<Class<? extends Event>, EventHandler<? extends Event>>();

//        block.accept(new EventHandlerRegistry() {
//            @Override
//            public EventHandlerRegistry register(Class<Event> targetClass, EventHandler<Event> eventHandler) {
//                eventTypeToHandler.put(targetClass, eventHandler);
//                return this;
//            }
//        });

        block.accept(new EventHandlerRegistry() {
            @Override
            public <T extends Event> EventHandlerRegistry register(Class<T> eventType, EventHandler<T> eventHandler) {
                eventTypeToHandler.put(eventType, eventHandler);
                return this;
            }
        });

        return eventType -> Optional.ofNullable((EventHandler<Event>) eventTypeToHandler.get(eventType));
    }

    Optional<EventHandler<Event>> resolve(Class<? extends Event> eventType);

    @FunctionalInterface
    interface EventHandlerRegistry {

        <T extends Event> EventHandlerRegistry register(Class<T> eventType, EventHandler<T> eventHandler);
    }
}
