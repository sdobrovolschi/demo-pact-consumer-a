package com.example.pact.consumer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@FeignClient(name = "customers", url = "${customer-service.base-url}")
interface CustomersApi {

    @GetMapping(path = "/customers/{customerId}", produces = APPLICATION_JSON_VALUE)
    Optional<Customer> find(@PathVariable("customerId") String customerId);
}
