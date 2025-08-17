package com.microservicetwo.microservice_notification_dispatcher.domain.model;

public class User {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private UserStatus status;

    public User(Long id, String name, String email, String phoneNumber, UserStatus status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.status = status != null ? status : UserStatus.ACTIVE;
    }

    public User(){
        this.status = UserStatus.ACTIVE;
    }

    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }

    public UserStatus getStatus() {
        return this.status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }


    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public enum UserStatus {
        ACTIVE,
        INACTIVE,
        SUSPENDED
    }

}