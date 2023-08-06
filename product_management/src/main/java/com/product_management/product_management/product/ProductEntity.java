package com.product_management.product_management.product;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity(name = "products")
@AllArgsConstructor
@NoArgsConstructor
public class ProductEntity  {
    @Id
    UUID productId;
    String name;
    String description;
    int price;
    int stock;
}