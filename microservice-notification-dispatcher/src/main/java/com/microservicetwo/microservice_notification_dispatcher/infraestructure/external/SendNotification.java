package com.microservicetwo.microservice_notification_dispatcher.infraestructure.external;

import com.microservicetwo.microservice_notification_dispatcher.domain.model.Notification;
import com.microservicetwo.microservice_notification_dispatcher.domain.port.out.ISendNotification;
import reactor.core.publisher.Mono;

public class SendNotification implements ISendNotification{

    @Override
    public Mono<Boolean> sendEmail(Notification notification) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendEmail'");
    }

    @Override
    public Mono<Boolean> sendSMS(Notification notification) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendSMS'");
    }
    
}