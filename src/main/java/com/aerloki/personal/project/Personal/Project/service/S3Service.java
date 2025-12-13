package com.aerloki.personal.project.Personal.Project.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3Service {
    
    private final S3Client s3Client;
    private static final String BUCKET_NAME = "product-images";
    
    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
        initializeBucket();
    }
    
    private void initializeBucket() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(BUCKET_NAME).build());
            System.out.println("✓ S3 bucket '" + BUCKET_NAME + "' already exists");
        } catch (Exception e) {
            try {
                s3Client.createBucket(CreateBucketRequest.builder().bucket(BUCKET_NAME).build());
                System.out.println("✓ Created S3 bucket: " + BUCKET_NAME);
            } catch (Exception ex) {
                System.err.println("Failed to create S3 bucket: " + ex.getMessage());
            }
        }
    }
    
    public String uploadImage(String key, byte[] imageData, String contentType) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(key)
                    .contentType(contentType)
                    .build();
            
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(imageData));
            
            // Return the URL that can be used to access the image
            return String.format("http://localhost:4566/%s/%s", BUCKET_NAME, key);
        } catch (Exception e) {
            System.err.println("Failed to upload image: " + e.getMessage());
            return null;
        }
    }
    
    public String getBucketName() {
        return BUCKET_NAME;
    }
}
