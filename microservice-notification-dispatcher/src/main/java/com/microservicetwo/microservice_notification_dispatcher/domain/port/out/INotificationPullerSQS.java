package com.microservicetwo.microservice_notification_dispatcher.domain.port.out;

import com.microservicetwo.microservice_notification_dispatcher.domain.model.SQSMessage;
import reactor.core.publisher.Flux;

public interface INotificationPullerSQS {
    Flux<SQSMessage> pullNotifications();
}