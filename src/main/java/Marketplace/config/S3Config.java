package Marketplace.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {
  @Value("us-east-1")
  private Region region;

  @Bean
  public S3Client s3Client() {
    return S3Client.builder()
      .region(region)
      .credentialsProvider(ProfileCredentialsProvider.create("privium-dev"))
      .build();
  }
}
