package com.order_management.order_management.order.dto;

import java.util.List;
import java.util.UUID;

import lombok.Data;
import lombok.Getter;

@Data
public class OrderDto {
    List<Item> orderedItems;

    @Getter
    public static class Item {
        UUID productId;
        int quantity;
    }
}