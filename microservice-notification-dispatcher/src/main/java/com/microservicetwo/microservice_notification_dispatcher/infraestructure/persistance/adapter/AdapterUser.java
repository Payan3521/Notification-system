package com.microservicetwo.microservice_notification_dispatcher.infraestructure.persistance.adapter;

import java.util.Optional;
import org.springframework.stereotype.Component;
import com.microservicetwo.microservice_notification_dispatcher.domain.model.User;
import com.microservicetwo.microservice_notification_dispatcher.domain.port.out.IPortUser;
import com.microservicetwo.microservice_notification_dispatcher.infraestructure.persistance.entity.UserEntity;
import com.microservicetwo.microservice_notification_dispatcher.infraestructure.persistance.mapper.UserMapper;
import com.microservicetwo.microservice_notification_dispatcher.infraestructure.persistance.repository.ORMuser;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AdapterUser implements IPortUser {

    private final ORMuser ormUser;
    private final UserMapper userMapper;

    @Override
    public boolean existsByEmail(String email) { //no se estan usando
        return ormUser.existsByEmail(email);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {   //no se estan usando
        return ormUser.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        Optional<UserEntity> userEntity = ormUser.findByEmail(email);
        return userEntity.map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        Optional<UserEntity> userEntity = ormUser.findByPhoneNumber(phoneNumber);
        return userEntity.map(userMapper::toDomain);
    }

    @Override
    public User saveUser(User user) {
        UserEntity userEntity = userMapper.toEntity(user);
        UserEntity savedEntity = ormUser.save(userEntity);
        return userMapper.toDomain(savedEntity);
    }
    
}