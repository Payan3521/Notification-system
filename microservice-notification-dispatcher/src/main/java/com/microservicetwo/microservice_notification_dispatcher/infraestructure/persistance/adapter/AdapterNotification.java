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

    @Override
    public Notification saveNotification(Notification notification) {
        NotificationEntity notificationEntity = notificationMapper.toEntity(notification);
        NotificationEntity savedEntity = ormNotification.save(notificationEntity);
        return notificationMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Notification> updateNotification(String notificationId, Notification notification) {
        Optional<NotificationEntity> existingEntity = ormNotification.findById(notificationId);
        if (existingEntity.isPresent()) {
            NotificationEntity notificationSaved = existingEntity.get();

            notificationSaved.setInfo(notification.getInfo());
            notificationSaved.setSubject(notification.getSubject());
            notificationSaved.setBody(notification.getBody());
            notificationSaved.setChannel(notification.getChannel().name());
            notificationSaved.setStatus(notification.getStatus().name());
            notificationSaved.setCreatedAt(notification.getCreatedAt());
            notificationSaved.setSentTime(notification.getSentTime());
            notificationSaved.setRetryCount(notification.getRetryCount());

            NotificationEntity updatedEntity = ormNotification.save(notificationSaved);
            return Optional.of(notificationMapper.toDomain(updatedEntity));
        } else {
            return Optional.empty();
        }
    }

}