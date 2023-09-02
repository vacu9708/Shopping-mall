package com.product_management.product_management.product.saga_;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.product_management.product_management.product.ProductRepository;
import com.product_management.product_management.product.dto_.NewProductDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SagaOrchestrator {
    final ProductRepository productRepository;
    final AmazonS3 amazonS3;
    final TransactionTemplate transactionTemplate;

    public String addProduct(PutObjectRequest putObjectRequest, NewProductDto newProductDto, String imgLocation) {
        // Upload product image to S3
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            try {
                amazonS3.putObject(putObjectRequest);
                return "OK";
            } catch (AmazonServiceException e) {
                return e.getErrorMessage();
            } catch (Exception e) {
                return e.getMessage();
            }
        });
        // Add product to DB
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            transactionTemplate.execute(status -> {
                try {
                    productRepository.addProduct(newProductDto.getName(), newProductDto.getDescription(), newProductDto.getPrice(), newProductDto.getStock(), imgLocation);
                return "OK";
                } catch (Exception e) {
                    return "DB_ERROR";
                } 
            });
            return "OK";
        });

        // Join futures and prepare for compensations
        Deque<Runnable> rollbacks = new LinkedList<>();
        Boolean success = true;
        String errMsg = "";
        
        String future1Result = future1.join();
        if(future1Result.equals("OK")){
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
            errMsg += future1Result+"\n";
        }

        String future2Result = future2.join();
        if(future2Result.equals("OK")){
            // Prepare for compensation
            rollbacks.push(() -> {
                productRepository.deleteByName(newProductDto.getName());
            });
        }
        else{
            success = false;
            errMsg += future2Result+"\n";
        }

        // Complete the transaction or perform compensation if necessary
        if(success) {
            return "OK";
        }
        else {
            while(!rollbacks.isEmpty())
                rollbacks.pop().run();
            return errMsg;
        }    
    }
}
