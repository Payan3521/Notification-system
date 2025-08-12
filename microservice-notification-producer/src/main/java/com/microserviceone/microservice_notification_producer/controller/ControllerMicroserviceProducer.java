package com.microserviceone.microservice_notification_producer.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/producer")
public class ControllerMicroserviceProducer {
    @GetMapping("/status")
    public String getStatus() {
        return "Microservice Notification Producer is running";
    }
}