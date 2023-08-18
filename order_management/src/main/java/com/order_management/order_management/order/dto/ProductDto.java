package com.order_management.order_management.order.dto;

import lombok.Data;

@Data
public class ProductDto {
    String name;
    String description;
    int price;
    int stock;
    String imgLocation;
}
