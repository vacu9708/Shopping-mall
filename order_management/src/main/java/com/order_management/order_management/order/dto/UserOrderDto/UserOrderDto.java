package com.order_management.order_management.order.dto.UserOrderDto;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserOrderDto {
    UUID orderId;
    Timestamp orderDate;
    List<DetailedOrderedItem> detailedOrderedItems;
}