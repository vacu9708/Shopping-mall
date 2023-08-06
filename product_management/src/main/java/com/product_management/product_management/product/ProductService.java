package com.product_management.product_management.product;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.product_management.product_management.product.Dto.*;

@Component
public class ProductService {
    ProductRepository productRepository;
    ResponseEntity<String> addProduct(NewProductDto newProductDto) {
        productRepository.addProduct(newProductDto.getName(), newProductDto.getDescription(), newProductDto.getPrice(), newProductDto.getStock());

        return ResponseEntity.ok("");
    }

    ResponseEntity<String> setStock(String accessToken, int productId, int stock) {
        // Check if the user is an admin
        if(!JwtUtils.isAdmin(accessToken))
            return ResponseEntity.badRequest().body("NOT_ADMIN");

        productRepository.setStock(stock, Integer.toString(productId));
        return ResponseEntity.ok("");
    }
}
