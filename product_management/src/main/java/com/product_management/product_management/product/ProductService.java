package com.product_management.product_management.product;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.joda.time.LocalDateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
// import org.springframework.web.reactive.function.client.WebClient;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.product_management.product_management.product.Dto.*;
import com.product_management.product_management.product.Saga.SagaOrchestrator;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductService {
    final ProductRepository productRepository;
    final AmazonS3 amazonS3;
    final SagaOrchestrator sagaOrchestrator;

    ResponseEntity<String> addProduct(NewProductDto newProductDto) {
        // Check if the same product name already exists
        if(productRepository.existsByName(newProductDto.getName()) != false)
            return ResponseEntity.badRequest().body("PRODUCT_NAME_EXISTS");

        String imgLocation = "shopping_mall/products/"+newProductDto.getName()+" "+LocalDateTime.now().toString()+".jpg";
        String base64Img = newProductDto.getProductImg();
        // Convert base64Img to Inputstream
        InputStream productImg = new ByteArrayInputStream(Base64.getDecoder().decode(base64Img));
        // metadata
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(newProductDto.getProductImgSize());
        // Object to upload
        PutObjectRequest putObjectRequest = new PutObjectRequest("yasvacu", imgLocation, productImg, metadata)
            .withCannedAcl(com.amazonaws.services.s3.model.CannedAccessControlList.PublicRead);
        // Execute saga
        ResponseEntity<String> result = sagaOrchestrator.addProduct(putObjectRequest, newProductDto, imgLocation);
        // Response
        return result;
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