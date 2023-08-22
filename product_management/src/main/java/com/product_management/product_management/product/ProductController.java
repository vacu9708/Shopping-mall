package com.product_management.product_management.product;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.product_management.product_management.product.dto.*;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProductController {
    final ProductService productService;

    @GetMapping("/managerTest")
    ResponseEntity<String> managerTest(HttpServletRequest request) {
        // System.out.println(request.getRemoteAddr());
        return ResponseEntity.ok("test!");
    }

    @PostMapping("/manager/addProduct")
    ResponseEntity<String> managerAddProduct(HttpServletRequest request, @RequestBody NewProductDto productDto) {
        return productService.addProduct(productDto);
    }

    @GetMapping("/getProducts/{howMany}/{page}")
    ResponseEntity<List<ProductEntity>> getProducts(@PathVariable int howMany, @PathVariable int page) {
        return productService.getProducts(howMany, page);
    }

    @GetMapping("/getProduct/{productId}")
    ResponseEntity<?> getProduct(@PathVariable UUID productId) {
        return productService.getProduct(productId);
    }

    @PatchMapping("/manager/setStock/{productId}/{stockChange}")
    ResponseEntity<String> managerSetStock(HttpServletRequest request,
                                            @PathVariable UUID productId,
                                            @PathVariable int stockChange) {
        return productService.setStock(productId, stockChange);
    }

    @DeleteMapping("/manager/deleteProduct/{productId}")
    ResponseEntity<String> managerDeleteProduct(HttpServletRequest request, @PathVariable UUID productId) {
        return productService.deleteProduct(productId);
    }

    @GetMapping("/searchProducts/{query}/{howMany}/{page}")
    ResponseEntity<List<ProductEntity>> searchProducts(@PathVariable String query,
                                                        @PathVariable int howMany,
                                                        @PathVariable int page) {
        return productService.searchProducts(query, howMany, page);
    }
}
