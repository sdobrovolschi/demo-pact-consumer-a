package com.example.pact.consumer;

import java.util.Objects;

public final class Customer {

    private final CustomerId customerId;
    private final Name name;

    public Customer(CustomerId customerId, Name name) {
        this.customerId = customerId;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Customer customer = (Customer) o;
        return Objects.equals(customerId, customer.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(customerId);
    }
}
