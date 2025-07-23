package Marketplace.services;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3Service {
    private final S3Client s3;

    @Value("privium-photos")
    private String bucket;

    public S3Service(S3Client s3) {
        this.s3 = s3;
    }

    /**
     * Sube un archivo al bucket y retorna la URL pública.
     */
    public String uploadFile(MultipartFile file) throws IOException {
        String original = file.getOriginalFilename(); // "residence-proof-1 (1).jpg"
        String clean = original.replaceAll("[\\s()]", "_"); // "residence-proof-1__1_.jpg"

        String key = "publicaciones/" + UUID.randomUUID() + "-" + clean;

        s3.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentType(file.getContentType())
                        .build(),
                RequestBody.fromBytes(file.getBytes()));

        return String.format("https://%s.s3.amazonaws.com/%s", bucket, key);
    }

    /**
     * Elimina un objeto del bucket dado su key.
     */
    public void deleteFile(String key) {
        s3.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build());
    }

    /**
     * Extrae la 'key' de S3 a partir de la URL completa.
     * Por ejemplo, de
     * "https://mi-bucket.s3.amazonaws.com/publicaciones/uuid-nombre.jpg"
     * retorna "publicaciones/uuid-nombre.jpg".
     */
    public String extractKey(String fileUrl) {
        try {
            URI uri = new URI(fileUrl);
            String path = uri.getPath(); // "/publicaciones/uuid-nombre.jpg"
            if (path.startsWith("/")) {
                return path.substring(1); // "publicaciones/uuid-nombre.jpg"
            }
            return path;
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("URL inválida para extraer key: " + fileUrl, e);
        }
    }
}
