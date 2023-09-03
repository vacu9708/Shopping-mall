package com.product_management.product_management.product.dto_;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class NewProductDto {
    String name;
    String description;
    int price;
    int stock;
    String productImg;
}
