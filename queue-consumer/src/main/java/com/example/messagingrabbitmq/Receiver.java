package com.example.messagingrabbitmq;

import org.springframework.stereotype.Component;

@Component
public class Receiver {

	public void receiveMessage(String message) throws Exception {
		System.out.println("Received <" + message + ">");
		
		// Simulate some work
		Thread.sleep(10);
	}
}
