package com.ohs.monolithic.common.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {
/*
  @Value("${spring.cloud.aws.credentials.access-key}")
  private String accessKey;

  @Value("${spring.cloud.aws.credentials.secret-key}")
  private String secretKey;
  @Value("${spring.cloud.aws.region.static}")
  private String region;

  @Bean
  @ConditionalOnProperty(name = "spring.cloud.aws.credentials.access-key")
  public S3Client s3Client() {

    if(accessKey == null || secretKey == null || region == null)
      return null;
    if(accessKey.isEmpty() || secretKey.isEmpty() || region.isEmpty())
      return null;

    return S3Client.builder()
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
            .build();
  }*/
}
