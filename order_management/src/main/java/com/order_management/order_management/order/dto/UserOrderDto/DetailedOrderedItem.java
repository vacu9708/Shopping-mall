package com.order_management.order_management.order.dto.UserOrderDto;

import com.order_management.order_management.order.dto.ProductDto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DetailedOrderedItem {
    ProductDto product;
    int quantity;
}