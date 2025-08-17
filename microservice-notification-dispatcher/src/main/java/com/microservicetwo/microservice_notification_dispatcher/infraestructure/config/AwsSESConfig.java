package com.microservicetwo.microservice_notification_dispatcher.infraestructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.ses.SesAsyncClient;

@Configuration
public class AwsSESConfig {
    @Bean
    public SesAsyncClient sesAsyncClient(@Value("${aws.region}") String region) {
        return SesAsyncClient.builder()
            .region(Region.of(region))
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build();
    }   
}
