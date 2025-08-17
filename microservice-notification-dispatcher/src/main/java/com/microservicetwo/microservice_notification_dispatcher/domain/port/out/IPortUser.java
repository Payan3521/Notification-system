package com.microservicetwo.microservice_notification_dispatcher.domain.port.out;

import java.util.Optional;
import com.microservicetwo.microservice_notification_dispatcher.domain.model.User;

public interface IPortUser {
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);
    User saveUser(User user);
}