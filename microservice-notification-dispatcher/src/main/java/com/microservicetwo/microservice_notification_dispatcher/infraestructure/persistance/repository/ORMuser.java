package com.microservicetwo.microservice_notification_dispatcher.infraestructure.persistance.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.microservicetwo.microservice_notification_dispatcher.infraestructure.persistance.entity.UserEntity;

@Repository
public interface ORMuser extends JpaRepository<UserEntity, Long> {
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByPhoneNumber(String phoneNumber);
}