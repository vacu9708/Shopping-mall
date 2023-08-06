package com.product_management.product_management.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<ProductEntity, String>{
    @Modifying
    @Query(value = "INSERT INTO users (name, description, price, stock) VALUES (?1, ?2, ?3, ?4)", nativeQuery = true)
    void addProduct(String name, String description, int price, int stock);
    
    @Modifying
    @Query(value = "UPDATE users SET stock = ?1 WHERE product_id = ?2", nativeQuery = true)
    void setStock(int stock, String productId);

    @Query(value = "SELECT * FROM products LIMIT ?1 OFFSET ?2", nativeQuery = true)
    List<ProductEntity> getProducts(int howMany, int page);
}
