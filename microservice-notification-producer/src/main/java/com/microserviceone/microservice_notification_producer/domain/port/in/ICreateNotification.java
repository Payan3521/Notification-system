package com.microserviceone.microservice_notification_producer.domain.port.in;

import com.microserviceone.microservice_notification_producer.domain.model.Notification;

public interface ICreateNotification {

    Notification createNotification(Notification notification);
    
}
