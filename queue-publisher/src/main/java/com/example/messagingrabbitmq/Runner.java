package com.example.messagingrabbitmq;

import java.util.concurrent.TimeUnit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import org.springframework.amqp.core.DirectExchange;

@Component
public class Runner implements CommandLineRunner {

	private final RabbitTemplate rabbitTemplate;

	public Runner(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	@Override
	public void run(String... args) throws Exception {

		while(true){

			System.out.println("Sending message...");

			// Send message
			rabbitTemplate.convertAndSend("", "spring-boot", "Hello from RabbitMQ!");
			
			// Sleep 1 second
			// Thread.sleep(1000);
		}
	}

}
