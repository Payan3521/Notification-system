package com.microservicetwo.microservice_notification_dispatcher.domain.port.out;

import com.microservicetwo.microservice_notification_dispatcher.domain.model.Notification;
import reactor.core.publisher.Mono;

public interface ISendNotification {
    Mono<Boolean> sendEmail(Notification notification);
    Mono<Boolean> sendSMS(Notification notification);
}