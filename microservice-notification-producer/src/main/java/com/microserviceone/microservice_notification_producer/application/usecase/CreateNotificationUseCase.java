package com.microserviceone.microservice_notification_producer.application.usecase;

import org.springframework.stereotype.Service;

import com.microserviceone.microservice_notification_producer.domain.model.Notification;
import com.microserviceone.microservice_notification_producer.domain.port.in.ICreateNotification;

@Service
public class CreateNotificationUseCase implements ICreateNotification{

    @Override
    public Notification createNotification(Notification notification) {
        getChannel(notification.getInfo());
        createEvent(notification);

        return notification;
    }
    
    private void getChannel(String info){
        //Crear metodo de cambio
    }

    private void createEvent(Notification notification){
        //Crear metodo de evento
    }
}