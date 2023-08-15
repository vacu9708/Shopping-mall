package com.product_management.product_management.product.Saga;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Component;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.product_management.product_management.product.ProductRepository;
import com.product_management.product_management.product.Dto.NewProductDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SagaOrchestrator {
    final ProductRepository productRepository;
    final AmazonS3 amazonS3;

    public String addProduct(PutObjectRequest putObjectRequest, NewProductDto newProductDto, String imgLocation) {
        // Upload product image to S3
        CompletableFuture<Boolean> future1 = CompletableFuture.supplyAsync(() -> {
            try {
                amazonS3.putObject(putObjectRequest);
                return true;
            } catch (AmazonServiceException e) {
                System.err.println(e.getErrorMessage());
                return false;
            }
        });
        // Add product to DB
        CompletableFuture<Boolean> future2 = CompletableFuture.supplyAsync(() -> {
            try {
                productRepository.addProduct(newProductDto.getName(), newProductDto.getDescription(), newProductDto.getPrice(), newProductDto.getStock(), imgLocation);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            
        });

        // Join futures and prepare for compensations
        Deque<Runnable> rollbacks = new LinkedList<>();
        Boolean success = true;
        String errMsg = "";
        
        if(future1.join()){
            // Prepare for compensation
            rollbacks.push(() -> {
                try {
                    amazonS3.deleteObject("yasvacu", imgLocation);
                } catch (AmazonServiceException e) {
                    System.out.println(e.getErrorMessage());
                }
            });
        }
        else{
            success = false;
            errMsg = "S3_ERROR";
        }

        if(future2.join()){
            // Prepare for compensation
            rollbacks.push(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                productRepository.deleteByName(newProductDto.getName());
            });
        }
        else{
            success = false;
            errMsg = "DB_ERROR";
        }

        if(success) {
            return "OK";
        }
        // Perform compensation if necessary
        else {
            while(!rollbacks.isEmpty())
                rollbacks.pop().run();
            return errMsg;
        }    
    }
}
