package com.microservicetwo.microservice_notification_dispatcher.infraestructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.regions.Region;

@Configuration
public class AwsSQSConfig {


    @Bean
    public SqsAsyncClient sqsAsyncClient(@Value("${aws.region}") String region) {
        return SqsAsyncClient.builder()
            .region(Region.of(region))
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build();
    }

}