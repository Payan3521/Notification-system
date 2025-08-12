package com.microservicetwo.microservice_notification_dispatcher.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dispatcher")
public class ControllerMicroserviceDispatcher {
    @GetMapping("/status")
    public String getStatus() {
        return "Microservice Notification Dispatcher is running";
    }
}