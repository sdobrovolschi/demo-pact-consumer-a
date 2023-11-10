package com.example.pact.consumer;

import au.com.dius.pact.consumer.dsl.PactBuilder;
import au.com.dius.pact.consumer.junit5.PactConsumerTest;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Interaction;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.amqp.core.MessagePropertiesBuilder;
import org.springframework.amqp.rabbit.core.RabbitOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestPropertySource;

import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonBody;
import static au.com.dius.pact.consumer.junit5.ProviderType.ASYNCH;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.verify;
import static org.springframework.amqp.core.MessageBuilder.withBody;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

// https://github.com/pact-foundation/pact-jvm/issues/610
//https://github.com/pact-foundation/pact-jvm/blob/master/consumer/junit5/src/test/java/au/com/dius/pact/consumer/junit5/V4PactBuilderTest.java
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@PactConsumerTest
@PactTestFor(providerName = "provider")
@TestPropertySource("classpath:/application-test.properties")
class MessageDispatcherTest {

    @Autowired
    RabbitOperations rabbit;

    @Autowired
    EventHandler<CustomerCreated> eventHandler;

    @Captor
    ArgumentCaptor<CustomerCreated> eventCaptor;

    @Pact(consumer = "consumer-a")
    V4Pact customerCreated(PactBuilder builder) {
        return builder
                .given("John Snow customer is created")
                .expectsToReceiveMessageInteraction("a customer created event is received", messageBuilder ->
                        messageBuilder.withContents(message -> message
                                .withMetadata(metadata -> metadata
                                        .includesStr("type", "customerCreated")
                                        .includesStr("contentType", APPLICATION_JSON_VALUE))
                                .withContent(newJsonBody(event -> event
                                        .stringValue("customerId", "1")
                                        .stringValue("name", "John Snow")
                                ).build())))
                .toPact();
    }

    //    https://github.com/santiagovm/pact-demo-spring-rabbitmq-protobuf
    @Test
    @PactTestFor(pactMethod = "customerCreated", providerType = ASYNCH)
    void receiving(V4Interaction.AsynchronousMessage message) throws Exception {

        rabbit.send("messages", "",
                withBody(message.contentsAsBytes())
                        .andProperties(MessagePropertiesBuilder.newInstance()
                                .setType(message.getMetadata().get("type").toString())
                                .setContentType(message.getMetadata().get("contentType").toString())
                                .build()
                        )
                        .build());

        await("event").atMost(5, SECONDS)
                .untilAsserted(() -> verify(eventHandler).handle(eventCaptor.capture()));

        assertThat(eventCaptor.getValue())
                .usingRecursiveComparison()
                .isEqualTo(new CustomerCreated("1", "John Snow"));
    }
}
