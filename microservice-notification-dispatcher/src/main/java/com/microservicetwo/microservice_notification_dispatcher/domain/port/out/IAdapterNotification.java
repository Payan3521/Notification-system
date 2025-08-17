package com.microservicetwo.microservice_notification_dispatcher.domain.port.out;

import java.util.Optional;
import com.microservicetwo.microservice_notification_dispatcher.domain.model.Notification;

public interface IAdapterNotification {
    Optional<Notification> GetByIdNotification(String id);
    Notification saveNotification(Notification notification);
    Notification updateNotification(Notification notification);
}