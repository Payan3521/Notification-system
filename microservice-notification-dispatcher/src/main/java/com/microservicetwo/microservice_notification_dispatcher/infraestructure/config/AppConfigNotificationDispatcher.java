package com.microservicetwo.microservice_notification_dispatcher.infraestructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.microservicetwo.microservice_notification_dispatcher.application.service.NotificationService;
import com.microservicetwo.microservice_notification_dispatcher.domain.port.in.IGetNotificationById;

@Configuration
public class AppConfigNotificationDispatcher {
    @Bean
    public NotificationService notificationService(IGetNotificationById getNotificationById) {
        return new NotificationService(getNotificationById);
    }
}