package com.microservicetwo.microservice_notification_dispatcher.infraestructure.persistance.adapter;

import java.util.Optional;
import org.springframework.stereotype.Component;
import com.microservicetwo.microservice_notification_dispatcher.domain.model.Notification;
import com.microservicetwo.microservice_notification_dispatcher.domain.port.out.IAdapterNotification;
import com.microservicetwo.microservice_notification_dispatcher.infraestructure.persistance.entity.NotificationEntity;
import com.microservicetwo.microservice_notification_dispatcher.infraestructure.persistance.mapper.NotificationMapper;
import com.microservicetwo.microservice_notification_dispatcher.infraestructure.persistance.repository.ORMnotification;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AdapterNotification implements IAdapterNotification{

    private final ORMnotification ormNotification;
    private final NotificationMapper notificationMapper;

    @Override
    public Optional<Notification> GetByIdNotification(String id) {
            Optional<NotificationEntity> notificationEntity = ormNotification.findById(id);
    
            if (notificationEntity.isPresent()) {
                Notification notification = notificationMapper.toDomain(notificationEntity.get());
                return Optional.of(notification);
            }else{
                return Optional.empty();
            }   
    
    }

}
