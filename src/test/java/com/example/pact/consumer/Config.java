package com.example.pact.consumer;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.example.pact.consumer.EventHandlerResolver.newResolver;

@Configuration
class Config {

    @MockBean
    EventHandler<CustomerCreated> eventHandler;

    @Bean
    EventHandlerResolver eventHandlerResolver() {
        return newResolver(registry -> registry
                .register(CustomerCreated.class, eventHandler)
        );
    }
}
