package com.product_management.product_management.product.Dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProductDto {
    String name;
    String description;
    int price;
    int stock;
    String productImg;
    int productImgSize;
}
