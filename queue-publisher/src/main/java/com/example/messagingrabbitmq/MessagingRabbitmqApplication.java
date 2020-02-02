package com.example.messagingrabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MessagingRabbitmqApplication {

	static final String queueName = System.getenv("QUEUE_NAME");

	@Bean
	Queue queue() {
		return new Queue(queueName, false);
	}

	@Bean
	Binding binding(Queue queue) {
		return BindingBuilder.bind(queue).to(DirectExchange.DEFAULT).with(queueName);
	}

	 @Bean
    public ConnectionFactory rabbitConnectionFactory() {
        CachingConnectionFactory connectionFactory =
            new CachingConnectionFactory("rabbitmq.default.svc.cluster.local");
        connectionFactory.setUsername(System.getenv("RABBITMQ_USER"));
        connectionFactory.setPassword(System.getenv("RABBITMQ_PASSWORD"));
        return connectionFactory;
    }

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(MessagingRabbitmqApplication.class, args).close();
	}

}
