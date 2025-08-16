package com.microservicetwo.microservice_notification_dispatcher.application.usecase;

import com.microservicetwo.microservice_notification_dispatcher.domain.exception.NotificationNotFoundException;
import com.microservicetwo.microservice_notification_dispatcher.domain.model.Notification;
import com.microservicetwo.microservice_notification_dispatcher.domain.port.in.IGetNotificationById;
import com.microservicetwo.microservice_notification_dispatcher.domain.port.out.IAdapterNotification;
import lombok.RequiredArgsConstructor;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetNotificationByIdUseCase implements IGetNotificationById {

    private final IAdapterNotification adapterNotification; 

    @Override
    public Optional<Notification> getByIdNotification(String id) {
        Optional<Notification> notification = adapterNotification.GetByIdNotification(id);

        if (notification.isPresent()) {
            return Optional.of(notification.get());   
        }else{
            throw new NotificationNotFoundException(id);
        }

    }
    
}