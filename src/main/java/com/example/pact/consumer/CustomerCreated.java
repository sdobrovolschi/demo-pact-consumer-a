package com.example.pact.consumer;

public record CustomerCreated(String customerId, String name) implements Event {
}
