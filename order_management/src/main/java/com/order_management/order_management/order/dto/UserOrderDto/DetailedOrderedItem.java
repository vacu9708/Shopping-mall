package com.order_management.order_management.order.dto.UserOrderDto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DetailedOrderedItem {
    OrderedProductDto orderedProduct;
    int quantity;
}