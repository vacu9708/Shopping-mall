package com.product_management.product_management.product;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.product_management.product_management.product.Dto.*;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProductController {
    final ProductService productService;

    @GetMapping("/test")
    ResponseEntity<String> test(HttpServletRequest request) {
        // System.out.println(request.getRemoteAddr());
        return ResponseEntity.ok("test");
    }

    @PostMapping("/manager/addProduct")
    ResponseEntity<String> addProduct(@RequestBody ProductDto productDto) {
        return productService.addProduct(productDto);
    }

    @GetMapping("/getProducts/{howMany}/{page}")
    ResponseEntity<?> getProducts(@PathVariable int howMany, @PathVariable int page) {
        return productService.getProducts(howMany, page);
    }

    @GetMapping("/getProduct/{productId}")
    ResponseEntity<?> getProduct(@PathVariable UUID productId) {
        return productService.getProduct(productId);
    }

    @PatchMapping("/manager/setStock/{productId}/{stockChange}")
    ResponseEntity<String> setStock(@PathVariable UUID productId,
                                    @PathVariable int stockChange) {
        return productService.setStock(productId, stockChange);
    }

    @DeleteMapping("/manager/deleteProduct/{productId}")
    ResponseEntity<String> deleteProduct(@PathVariable UUID productId) {
        return productService.deleteProduct(productId);
    }

    // @GetMapping("/searchProducts")
    // ResponseEntity<?> searchProducts(@RequestParam String query,
    //                                  @RequestParam int howMany,
    //                                  @RequestParam int page) {
    //     return productService.searchProducts(query, howMany, page);
    // }
}
