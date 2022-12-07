package com.example.pact.consumer;

import java.util.Optional;

public interface Customers {

    Optional<Customer> find(CustomerId customerId);
}
