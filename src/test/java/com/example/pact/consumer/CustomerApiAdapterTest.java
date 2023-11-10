package com.example.pact.consumer;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactBuilder;
import au.com.dius.pact.consumer.junit5.PactConsumerTest;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonBody;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.optional;

//https://pactflow.io/how-pact-works/#slide-1
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@PactConsumerTest
@PactTestFor(providerName = "provider")
@TestPropertySource("classpath:/application-test.properties")
class CustomerApiAdapterTest {

    @Autowired
    Customers customers;

    @BeforeEach
    void setUp(MockServer mockServer) {
        // Each test execution bootstraps a MockServer.
        // The interceptor addresses the port awareness issue of the Feign Client, which is configured once per TestContext,
        // when binding to a random port.
        BaseUrlRewriteInterceptor.setBaseUrl(mockServer.getUrl());
    }

    @AfterEach
    void cleanUp() {
        BaseUrlRewriteInterceptor.resetBaseUrls();
    }

    @Pact(consumer = "consumer-a")
    V4Pact customer(PactBuilder builder) {
        return builder
                .given("a customer with id 1")
                .expectsToReceiveHttpInteraction("a request for a customer", http -> http
                        .withRequest(request -> request
                                .method("GET")
                                .path("/customers/1"))
                        .willRespondWith(response -> response
                                .status(200)
                                .header("Content-Type", "application/json")
                                .body(newJsonBody(customer -> customer
                                        .stringValue("customerId", "1")
                                        .stringValue("name", "John Snow")
                                        .stringValue("email", "jsnow@test.com"))
                                        .build())))
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "customer")
    void findingKnown() {
        var customerId = new CustomerId("1");
        Optional<Customer> customer = customers.find(customerId);
        assertThat(customer)
                .usingRecursiveComparison()
                .asInstanceOf(optional(Customer.class))
                .contains(new Customer(customerId, new Name("John Snow")));
    }
}
