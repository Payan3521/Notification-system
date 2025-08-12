package com.microservicetwo.microservice_notification_dispatcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MicroserviceNotificationDispatcherApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceNotificationDispatcherApplication.class, args);
	}

}