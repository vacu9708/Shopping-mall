package com.product_management.product_management;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;


@Configuration
public class awsS3Config {
    @Bean
    public AmazonS3 s3Client() {
        // BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        return AmazonS3ClientBuilder.standard()
                .withRegion(Regions.AP_NORTHEAST_2)
                // .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }
}
