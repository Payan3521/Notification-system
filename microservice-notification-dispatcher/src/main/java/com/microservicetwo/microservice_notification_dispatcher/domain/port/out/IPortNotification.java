package com.microservicetwo.microservice_notification_dispatcher.domain.port.out;

import java.util.Optional;
import com.microservicetwo.microservice_notification_dispatcher.domain.model.Notification;

public interface IPortNotification {
    Optional<Notification> GetByIdNotification(String id);
    Notification saveNotification(Notification notification);
    Optional<Notification> updateNotification(String notificationId, Notification notification);
}