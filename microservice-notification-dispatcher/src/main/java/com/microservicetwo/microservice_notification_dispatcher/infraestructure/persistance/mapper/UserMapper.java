package com.microservicetwo.microservice_notification_dispatcher.infraestructure.persistance.mapper;

import org.springframework.stereotype.Component;

import com.microservicetwo.microservice_notification_dispatcher.domain.model.User;
import com.microservicetwo.microservice_notification_dispatcher.domain.model.User.UserStatus;
import com.microservicetwo.microservice_notification_dispatcher.infraestructure.persistance.entity.UserEntity;

@Component
public class UserMapper {
    
    public UserEntity toEntity(User user) {
        UserEntity userEntity = UserEntity.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .phoneNumber(user.getPhoneNumber())
            .status(user.getStatus().name())
            .build();
        
        return userEntity;
    }

    public User toDomain(UserEntity userEntity) {
        UserStatus status = UserStatus.valueOf(userEntity.getStatus());
        
        User user = new User(
            userEntity.getId(),
            userEntity.getName(),
            userEntity.getEmail(),
            userEntity.getPhoneNumber(),
            status
        );
        
        return user;
    }
}