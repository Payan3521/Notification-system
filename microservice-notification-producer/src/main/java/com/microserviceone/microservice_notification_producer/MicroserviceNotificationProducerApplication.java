package com.microserviceone.microservice_notification_producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MicroserviceNotificationProducerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceNotificationProducerApplication.class, args);
	}

}