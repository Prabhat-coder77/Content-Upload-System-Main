package com.example.coursecontent.storage;

import com.example.coursecontent.exception.NotFoundException;
import com.example.coursecontent.exception.StorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "app.storage.type", havingValue = "S3")
public class S3StorageService implements StorageService {

    private final S3Client s3Client;
    private final String bucket;
    private final String prefix;

    public S3StorageService(
            @Value("${app.storage.s3.bucket}") String bucket,
            @Value("${app.storage.s3.prefix}") String prefix,
            @Value("${app.storage.s3.region}") String region) {
        
        this.bucket = bucket;
        this.prefix = prefix;
        // In a real application, inject the client via configuration bean to support customized credentials provider easily.
        // For simplicity, we use default S3Client built which picks up AWS credentials from env vars.
        this.s3Client = S3Client.builder()
                .region(software.amazon.awssdk.regions.Region.of(region))
                .build();
    }

    @Override
    public String store(String originalFilename, InputStream inputStream, long size, String contentType) {
        String extension = "";
        int extIndex = originalFilename.lastIndexOf('.');
        if (extIndex > 0) {
            extension = originalFilename.substring(extIndex);
        }
        
        String generatedFilename = UUID.randomUUID().toString() + extension;
        String storageKey = prefix + generatedFilename;

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(storageKey)
                    .contentType(contentType)
                    .contentLength(size)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, size));
            
            return storageKey;
        } catch (Exception e) {
            throw new StorageException("Failed to store file to S3", e);
        }
    }

    @Override
    public Resource loadAsResource(String storageKey) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(storageKey)
                    .build();

            ResponseInputStream<GetObjectResponse> s3InputStream = s3Client.getObject(getObjectRequest);
            return new InputStreamResource(s3InputStream);
            
        } catch (NoSuchKeyException e) {
            throw new NotFoundException("Could not read file from S3: " + storageKey);
        } catch (Exception e) {
            throw new StorageException("Failed to load file from S3", e);
        }
    }

    @Override
    public void delete(String storageKey) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(storageKey)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            throw new StorageException("Failed to delete file from S3", e);
        }
    }
}
