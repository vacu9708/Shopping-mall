package com.product_management.product_management.product;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.joda.time.LocalDateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
// import org.springframework.web.reactive.function.client.WebClient;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.product_management.product_management.product.Dto.*;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductService {
    final ProductRepository productRepository;
    final AmazonS3 amazonS3;

    ResponseEntity<String> addProduct(NewProductDto newProductDto) {
        // Check if the same product name already exists
        if(productRepository.existsByName(newProductDto.getName()) != false)
            return ResponseEntity.badRequest().body("PRODUCT_NAME_EXISTS");

        String imgLocation = "shopping_mall/products/"+newProductDto.getName()+LocalDateTime.now().toString()+".jpg";
        String base64Img = newProductDto.getProductImg();
        // Convert base64Img to Inputstream
        InputStream productImg = new ByteArrayInputStream(Base64.getDecoder().decode(base64Img));
        // metadata
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(newProductDto.getProductImgSize());
        // Object to upload
        PutObjectRequest putObjectRequest = new PutObjectRequest("yasvacu", imgLocation, productImg, metadata)
            .withCannedAcl(com.amazonaws.services.s3.model.CannedAccessControlList.PublicRead);
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
                productRepository.deleteByName(newProductDto.getName());
            });
        }
        else{
            success = false;
            errMsg = "DB_ERROR";
        }

        if(success)
            return ResponseEntity.ok("OK");
        // Perform compensation if necessary
        else
            while(!rollbacks.isEmpty()){
                rollbacks.pop().run();
            }
            return ResponseEntity.internalServerError().body(errMsg);
    }

    ResponseEntity<List<ProductEntity>> getProducts(int howMany, int page) {
        return ResponseEntity.ok(productRepository.getProducts(howMany, page));
    }

    ResponseEntity<?> getProduct(UUID productId) {
        ProductEntity productEntity = productRepository.findById(productId).get();
        if (productEntity == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(productEntity);
    }

    ResponseEntity<String> setStock(UUID productId, int stockChange) {
        // Get accessToken claims
        // CompletableFuture<AccessTokenClaimsDto> promise1 = WebClient.create()
        //     .get()
        //     .uri("http://localhost:8080/user_management/verifyAccessToken")
        //     .header("accessToken", accessToken)
        //     .retrieve()
        //     .bodyToMono(AccessTokenClaimsDto.class)
        //     .toFuture();

        // Check if the user is an admin
        // AccessTokenClaimsDto accessTokenClaimsDto = promise1.join();
        // if (!accessTokenClaimsDto.getUsername().equals("admin")) {
        //     return ResponseEntity.status(403).body("NOT_ADMIN");
        // }
        // Check if the product exists
        if (productRepository.existsById(productId) == false) {
            return ResponseEntity.notFound().build();
        }

        productRepository.setStock(productId, stockChange);
        return ResponseEntity.ok("OK");
    }

    ResponseEntity<String> deleteProduct(UUID productId) {
        // Check if the product exists
        if (productRepository.existsById(productId) == false) {
            return ResponseEntity.notFound().build();
        }

        // Delete the product image from S3
        try {
            amazonS3.deleteObject("yasvacu", productRepository.findByproductId(productId).getImgLocation());
        } catch (AmazonServiceException e) {
            System.out.println(e.getErrorMessage());
            return ResponseEntity.internalServerError().body("S3_ERROR");
        }

        productRepository.deleteById(productId);
        return ResponseEntity.ok("OK");
    }

    ResponseEntity<List<ProductEntity>> searchProducts(String query, int howMany, int page) {
        List<ProductEntity> productEntities = productRepository.searchProducts(query, howMany, page);
        return ResponseEntity.ok(productEntities);
    }
}