package com.microserviceone.microservice_notification_producer.infraestructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.microserviceone.microservice_notification_producer.application.service.NotificationService;
import com.microserviceone.microservice_notification_producer.domain.port.in.ICreateNotification;

@Configuration
public class AppConfigNotificationProducer {
    @Bean
    public NotificationService notificationService(ICreateNotification createNotification) {
        return new NotificationService(createNotification);
    }
}