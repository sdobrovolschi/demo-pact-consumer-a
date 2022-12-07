package com.example.pact.consumer;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;
import java.util.Optional;

import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonBody;
import static au.com.dius.pact.core.model.PactSpecVersion.V3;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.optional;

//https://pactflow.io/how-pact-works/#slide-1
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "provider", pactVersion = V3)
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
    RequestResponsePact customer(PactDslWithProvider builder) {
        return builder
                .given("a customer with id 1")
                .uponReceiving("a request for a customer")
                .method("GET")
                .path("/customers/1")
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body(newJsonBody(customer -> customer
                                .stringValue("customerId", "1")
                                .stringValue("name", "John Snow")
                        ).build()
                )
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "customer", pactVersion = V3)
    void findingKnown() {
        var customerId = new CustomerId("1");
        Optional<Customer> customer = customers.find(customerId);
        assertThat(customer)
                .usingRecursiveComparison()
                .asInstanceOf(optional(Customer.class))
                .contains(new Customer(customerId, new Name("John Snow")));
    }
}
