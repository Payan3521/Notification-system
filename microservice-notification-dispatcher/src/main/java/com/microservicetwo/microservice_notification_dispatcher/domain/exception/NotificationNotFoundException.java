package com.microservicetwo.microservice_notification_dispatcher.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotificationNotFoundException extends RuntimeException {
    public NotificationNotFoundException(String notificationId) {
        super("Notification with ID " + notificationId + " not found.");
    }
}