package com.product_management.product_management.product;

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

    @PatchMapping("/setStock")
    ResponseEntity<String> setStock(@RequestHeader("accessToken") String accessToken,
                                    @PathVariable int productId,
                                    @PathVariable int stock) {
        return productService.setStock(accessToken, productId, stock);
    }

    @GetMapping("/getProducts")
    ResponseEntity<?> getProducts(int page, int howMany) {
        return productService.getProducts(page, howMany);
    }
}
