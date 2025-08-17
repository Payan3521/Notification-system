package com.microservicetwo.microservice_notification_dispatcher.domain.port.in;

import com.microservicetwo.microservice_notification_dispatcher.domain.model.Notification;
import reactor.core.publisher.Flux;

public interface IGetNotificationAndSend {
    Flux<Notification> getNotificationAndSend();
}