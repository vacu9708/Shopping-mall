package com.product_management.product_management.product;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.product_management.product_management.product.Dto.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProductController {
    final ProductService productService;
    @PostMapping("/addProduct")
    ResponseEntity<String> addProduct(@RequestBody NewProductDto newProductDto) {
        return productService.addProduct(newProductDto);
    }

    @GetMapping("/getProducts/{howMany}/{page}")
    ResponseEntity<?> getProducts(@PathVariable int howMany, @PathVariable int page) {
        return productService.getProducts(howMany, page);
    }

    @GetMapping("/getProduct/{productId}")
    ResponseEntity<?> getProduct(@PathVariable UUID productId) {
        return productService.getProduct(productId);
    }

    @PatchMapping("/setStock/{productId}/{stockChange}")
    ResponseEntity<String> setStock(@PathVariable UUID productId,
                                    @PathVariable int stockChange) {
        return productService.setStock(productId, stockChange);
    }

    @DeleteMapping("/deleteProduct/{productId}")
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
