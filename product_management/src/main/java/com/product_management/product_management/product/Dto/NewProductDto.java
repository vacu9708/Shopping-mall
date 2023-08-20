package com.product_management.product_management.product.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NewProductDto {
    String name;
    String description;
    int price;
    int stock;
    String productImg;
}
