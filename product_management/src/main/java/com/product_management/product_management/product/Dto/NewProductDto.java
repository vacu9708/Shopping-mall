package com.product_management.product_management.product.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class NewProductDto {
    String name;
    String description;
    int price;
    int stock;
    String productImg;
    int productImgSize;
}
