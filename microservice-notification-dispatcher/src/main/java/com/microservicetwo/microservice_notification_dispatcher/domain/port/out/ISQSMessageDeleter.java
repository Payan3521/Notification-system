package com.microservicetwo.microservice_notification_dispatcher.domain.port.out;

import reactor.core.publisher.Mono;

public interface ISQSMessageDeleter {
    Mono<Void> deleteMessage(String receiptHandle);
}