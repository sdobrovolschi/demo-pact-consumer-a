package com.example.pact.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
final class CustomerApiAdapter implements Customers {

    private final CustomersApi delegate;

    @Override
    public Optional<Customer> find(CustomerId customerId) {
        return delegate.find(customerId.stringValue());
    }
}
