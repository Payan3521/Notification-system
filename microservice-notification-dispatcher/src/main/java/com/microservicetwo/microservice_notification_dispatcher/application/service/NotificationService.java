package com.microservicetwo.microservice_notification_dispatcher.application.service;

import com.microservicetwo.microservice_notification_dispatcher.domain.port.in.IGetNotificationById;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NotificationService implements IGetNotificationById{
    private final IGetNotificationById getNotificationById;
}