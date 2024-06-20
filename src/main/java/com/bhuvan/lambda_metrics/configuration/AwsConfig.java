package com.bhuvan.lambda_metrics.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;


@Configuration
public class AwsConfig {
    @Value("${aws.accessKeyId}")
    private String accessKeyId;

    @Value("${aws.secretAccessKey}")
    private String secretAccessKey;

    @Value("${aws.region}")
    private String region;

    @Bean
    public CloudWatchClient cloudWatchClient() {
        System.out.println("accessKeyId: " + accessKeyId);
        System.out.println("secretAccessKey: " + secretAccessKey);
        System.out.println("region: " + region);
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                accessKeyId, secretAccessKey);

        return CloudWatchClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }
}
