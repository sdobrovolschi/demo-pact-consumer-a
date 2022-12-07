package com.example.pact.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@EnableFeignClients
public class ConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConsumerApplication.class, args);
	}

	@Configuration
	static class Config {

		@Bean
		Jackson2ObjectMapperBuilderCustomizer objectMapperBuilderCustomizer() {
			return builder -> builder
					.mixIn(Name.class, MixIns.NameMixIn.class);
		}
	}
}
