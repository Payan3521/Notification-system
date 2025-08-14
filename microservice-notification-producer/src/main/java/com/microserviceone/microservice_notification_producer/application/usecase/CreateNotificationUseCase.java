package com.microserviceone.microservice_notification_producer.application.usecase;

import java.util.regex.Pattern;
import org.springframework.stereotype.Service;
import com.microserviceone.microservice_notification_producer.domain.model.Notification;
import com.microserviceone.microservice_notification_producer.domain.model.Notification.Channel;
import com.microserviceone.microservice_notification_producer.domain.port.in.ICreateNotification;

@Service
public class CreateNotificationUseCase implements ICreateNotification{

    @Override
    public Notification createNotification(Notification notification) {
        getChannel(notification);
        createEvent(notification);

        return notification;
    }
    
    private void getChannel(Notification notification){
        if (Pattern.matches("^\\+?[0-9]+$", notification.getInfo())) {
            notification.setChannel(Channel.SMS);
        } 
        else if (Pattern.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", notification.getInfo())) {
            notification.setChannel(Channel.MAIL);
        } 
        else {
            notification.setChannel(Channel.UNKNOWN);
        }
    }

    private void createEvent(Notification notification){
        //Crear metodo de evento
    }
}