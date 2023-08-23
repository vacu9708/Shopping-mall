package com.order_management.order_management.order.dto.UserOrderDto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OrderedProductDto {
    String name;
    String description;
    int price;
    String imgLocation;
}
