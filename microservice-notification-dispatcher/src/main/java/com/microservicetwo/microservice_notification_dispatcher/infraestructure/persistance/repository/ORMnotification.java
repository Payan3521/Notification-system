package com.microservicetwo.microservice_notification_dispatcher.infraestructure.persistance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.microservicetwo.microservice_notification_dispatcher.infraestructure.persistance.entity.NotificationEntity;

@Repository
public interface ORMnotification extends JpaRepository<NotificationEntity, String> {
    
} 