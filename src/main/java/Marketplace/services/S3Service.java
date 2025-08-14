package Marketplace.services;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

@Service
public class S3Service {

    private final S3Client s3;
    private final S3Presigner presigner;

    @Value("${s3.private-bucket:privium-private}")
    private String privateBucket;

    // Duración por defecto de las URLs prefirmadas
    @Value("${s3.presign.ttl-minutes:10}")
    private long defaultTtlMinutes;

    public S3Service(S3Client s3, S3Presigner presigner) {
        this.s3 = s3;
        this.presigner = presigner;
    }

    /* ===================== PRIVADO ===================== */

    public String uploadPrivate(byte[] data, String filename) throws IOException {
        return uploadPrivate(data, filename, "application/octet-stream");
    }

    /** Sube al bucket privado. Devuelve la KEY (no URL). */
    public String uploadPrivate(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty() || !StringUtils.hasText(file.getOriginalFilename())) {
            throw new IllegalArgumentException("Archivo vacío o sin nombre");
        }
        String key = "verification-proofs/" + uuidName(file.getOriginalFilename());
        s3.putObject(
                PutObjectRequest.builder()
                        .bucket(privateBucket)
                        .key(key)
                        .contentType(file.getContentType())
                        .build(),
                RequestBody.fromBytes(file.getBytes()));
        return key; // guardá esta key en tu DB
    }

    /** Variante por bytes para tus “proofs”. */
    public String uploadPrivate(byte[] data, String filename, String contentType) throws IOException {
        if (data == null || data.length == 0 || !StringUtils.hasText(filename)) {
            throw new IllegalArgumentException("Archivo vacío o sin nombre");
        }
        String key = "verification-proofs/" + uuidName(filename);
        s3.putObject(
                PutObjectRequest.builder()
                        .bucket(privateBucket)
                        .key(key)
                        .contentType(contentType)
                        .build(),
                RequestBody.fromBytes(data));
        return key;
    }

    /** Borra un objeto del bucket privado. */
    public void deletePrivate(String key) {
        if (!StringUtils.hasText(key))
            return;
        try {
            s3.deleteObject(DeleteObjectRequest.builder()
                    .bucket(privateBucket)
                    .key(key)
                    .build());
        } catch (NoSuchKeyException ignored) {
            // ya no existe; idempotente
        } catch (S3Exception e) {
            throw e;
        }
    }

    /* ===================== PRESIGNED ===================== */

    /** Presigned GET para que el front pueda leer un objeto privado. */
    public String presignGet(String key, Long ttlMinutes) {
        if (!StringUtils.hasText(key))
            throw new IllegalArgumentException("key requerida");
        var req = GetObjectRequest.builder()
                .bucket(privateBucket)
                .key(key)
                .build();
        PresignedGetObjectRequest presigned = presigner.presignGetObject(b -> b
                .signatureDuration(Duration.ofMinutes(ttlMinutes != null ? ttlMinutes : defaultTtlMinutes))
                .getObjectRequest(req));
        return presigned.url().toString();
    }

    /** Presigned PUT para subida directa desde el navegador a bucket privado. */
    public String presignPut(String key, String contentType, Long ttlMinutes) {
        if (!StringUtils.hasText(key))
            throw new IllegalArgumentException("key requerida");
        var put = software.amazon.awssdk.services.s3.model.PutObjectRequest.builder()
                .bucket(privateBucket)
                .key(key)
                .contentType(contentType)
                .build();
        PresignedPutObjectRequest presigned = presigner.presignPutObject(b -> b
                .signatureDuration(Duration.ofMinutes(ttlMinutes != null ? ttlMinutes : defaultTtlMinutes))
                .putObjectRequest(put));
        return presigned.url().toString();
    }

    /* ===================== Utils ===================== */

    private static String uuidName(String original) {
        String clean = original.replaceAll("[\\s()]+", "_");
        return UUID.randomUUID() + "-" + clean;
    }

}
