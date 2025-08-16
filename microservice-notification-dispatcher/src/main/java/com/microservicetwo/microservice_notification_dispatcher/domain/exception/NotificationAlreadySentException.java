package com.microservicetwo.microservice_notification_dispatcher.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class NotificationAlreadySentException extends RuntimeException {
    public NotificationAlreadySentException() {
        super("Notification has already been sent.");
    }
}