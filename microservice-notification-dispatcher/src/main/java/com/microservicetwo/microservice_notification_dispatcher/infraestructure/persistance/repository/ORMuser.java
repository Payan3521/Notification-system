package com.microservicetwo.microservice_notification_dispatcher.infraestructure.persistance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ORMuser extends JpaRepository<UserEntity, Long>{
    
}