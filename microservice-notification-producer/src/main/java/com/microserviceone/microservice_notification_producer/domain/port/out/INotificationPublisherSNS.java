package com.microserviceone.microservice_notification_producer.domain.port.out;

import com.microserviceone.microservice_notification_producer.domain.model.Notification;
import reactor.core.publisher.Mono;

public interface INotificationPublisherSNS {
    Mono<Notification> publishNotification(Notification notification);
}