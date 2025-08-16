package com.microservicetwo.microservice_notification_dispatcher.infraestructure.persistance.mapper;

import com.microservicetwo.microservice_notification_dispatcher.domain.model.Notification;
import com.microservicetwo.microservice_notification_dispatcher.domain.model.Notification.Channel;
import com.microservicetwo.microservice_notification_dispatcher.domain.model.Notification.Status;
import com.microservicetwo.microservice_notification_dispatcher.infraestructure.persistance.entity.NotificationEntity;

public class NotificationMapper {
    
    public NotificationEntity toEntity(Notification notification){

        NotificationEntity notificationEntity = NotificationEntity.builder()
             .id(notification.getId())
             .info(notification.getInfo())
             .subject(notification.getSubject())
             .body(notification.getBody())
             .channel(notification.getChannel().name())
             .createdAt(notification.getCreatedAt())
             .status(notification.getStatus().name())
             .sentTime(notification.getSentTime())
             .retryCount(notification.getRetryCount())
        .build();
 
        return notificationEntity;
     }
 
     public Notification toDomain(NotificationEntity notificationEntity){
 
        Channel channel = Channel.valueOf(notificationEntity.getChannel());
        Status status = Status.valueOf(notificationEntity.getStatus());

        
        Notification notification = new Notification(
            notificationEntity.getId(),
            notificationEntity.getInfo(),
            notificationEntity.getSubject(),
            notificationEntity.getBody(),
            channel,
            notificationEntity.getCreatedAt(),
            status,
            notificationEntity.getSentTime(),
            notificationEntity.getRetryCount()
        );

        return notification;
    }
}