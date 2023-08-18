package com.order_management.order_management.order.entity;

import java.sql.Timestamp;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "orders")
public class OrderEntity {
    @Id
    UUID orderId;
    Timestamp orderDate;
    UUID userId;
}