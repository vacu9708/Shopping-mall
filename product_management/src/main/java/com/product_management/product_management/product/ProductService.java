package com.product_management.product_management.product;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.joda.time.LocalDateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

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
    aaaaaa
    ResponseEntity<String> addProduct(NewProductDto newProductDto) {
        // Check if the same product name already exists
        if(productRepository.findByName(newProductDto.getName()) != null)
            return ResponseEntity.badRequest().body("PRODUCT_NAME_EXISTS");

        String imgLocation = "shopping_mall/products/"+newProductDto.getName()+LocalDateTime.now().toString()+".jpg";
        String base64Img = newProductDto.getProductImg();
        // Convert base64Img to Inputstream
        InputStream productImg = new ByteArrayInputStream(Base64.getDecoder().decode(base64Img));
        // metadata
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(newProductDto.getProductImgSize());
        CompletableFuture<Boolean> future1 = CompletableFuture.supplyAsync(() -> {
            try {
                PutObjectRequest putObjectRequest = new PutObjectRequest("yasvacu", imgLocation, productImg, metadata)
                        .withCannedAcl(com.amazonaws.services.s3.model.CannedAccessControlList.PublicRead);
                amazonS3.putObject(putObjectRequest);
                return true;
            } catch (AmazonServiceException e) {
                // System.err.println(e.getErrorMessage());
                return false;
            }
        });
        // Add product
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            productRepository.addProduct(newProductDto.getName(), newProductDto.getDescription(), newProductDto.getPrice(), newProductDto.getStock(), imgLocation);
        });

        future1.join();
        future2.join();

        return ResponseEntity.ok("OK");
    }

    ResponseEntity<?> getProducts(int howMany, int page) {
        return ResponseEntity.ok(productRepository.getProducts(howMany, page));
    }

    ResponseEntity<?> getProduct(UUID productId) {
        ProductEntity productEntity = productRepository.findByProductId(productId);
        if (productEntity == null) {
            return ResponseEntity.notFound().build();
        }
        // Return productDto
        ProductDto productDto = ProductDto.builder()
            .name(productEntity.getName())
            .description(productEntity.getDescription())
            .price(productEntity.getPrice())
            .stock(productEntity.getStock())
            .productImg(productEntity.getImgLocation())
            .build();
        return ResponseEntity.ok(productDto);
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

        productRepository.setStock(productId, stockChange);
        return ResponseEntity.ok("OK");
    }

    ResponseEntity<String> deleteProduct(UUID productId) {
        // Check if the product exists
        if (productRepository.findByProductId(productId) == null) {
            return ResponseEntity.notFound().build();
        }
        productRepository.deleteByProductId(productId);
        return ResponseEntity.ok("OK");
    }

}
