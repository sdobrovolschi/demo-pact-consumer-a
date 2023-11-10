package com.example.pact.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class MessageDispatcher {

    private final EventHandlerResolver eventHandlerResolver;

    @RabbitListener(queues = "messages.handling")
    void dispatch(Message<Event> message) {
        var eventHandler = eventHandlerResolver.resolve(message.getPayload().getClass()).orElseThrow();

        eventHandler.handle(message.getPayload());
    }
}
