package Marketplace.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.regions.Region;

@Configuration
public class S3Config {

    @Value("${aws.region}")
    private String region;

    @Value("${AWS_ACCESS_KEY_ID:}")
    private String accessKey;

    @Value("${AWS_SECRET_ACCESS_KEY:}")
    private String secretKey;

    @Bean
    public S3Client s3Client() {
        // Si existen variables de entorno, usa StaticCredentialsProvider (Render)
        if (accessKey != null && !accessKey.isEmpty()) {
            return S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(
                            StaticCredentialsProvider.create(
                                    AwsBasicCredentials.create(accessKey, secretKey)
                            )
                    )
                    .build();
        }

        // Si no existen, usa perfil local de AWS (~/.aws/credentials)
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(ProfileCredentialsProvider.create("privium-dev"))
                .build();
    }
}
