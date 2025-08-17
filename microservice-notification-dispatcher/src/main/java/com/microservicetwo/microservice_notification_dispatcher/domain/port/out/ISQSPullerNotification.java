package com.microservicetwo.microservice_notification_dispatcher.domain.port.out;

import com.microservicetwo.microservice_notification_dispatcher.domain.model.SQSNotificationMessage;
import reactor.core.publisher.Flux;

public interface ISQSPullerNotification {
    Flux<SQSNotificationMessage> pullNotifications();
}