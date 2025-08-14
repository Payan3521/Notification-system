package com.microserviceone.microservice_notification_producer.application.service;

import com.microserviceone.microservice_notification_producer.domain.model.Notification;
import com.microserviceone.microservice_notification_producer.domain.port.in.ICreateNotification;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NotificationService implements ICreateNotification{

    private final ICreateNotification createNotification;

    @Override
    public Notification createNotification(Notification notification) {
        return createNotification.createNotification(notification);
    }
    
}