package com.microservicetwo.microservice_notification_dispatcher.domain.port.in;

import java.util.Optional;
import com.microservicetwo.microservice_notification_dispatcher.domain.model.Notification;

public interface IGetNotificationById {

    Optional <Notification> getByIdNotification(String id);
}
