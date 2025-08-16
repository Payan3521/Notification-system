package com.microservicetwo.microservice_notification_dispatcher.application.service;

import java.util.Optional;

import com.microservicetwo.microservice_notification_dispatcher.domain.model.Notification;
import com.microservicetwo.microservice_notification_dispatcher.domain.port.in.IGetNotificationById;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NotificationService implements IGetNotificationById{
    private final IGetNotificationById getNotificationById;

    @Override
    public Optional<Notification> getByIdNotification(String id) {
        
        Optional<Notification> notification = getNotificationById.getByIdNotification(id);

        return notification;
    }
}