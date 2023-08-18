package com.product_management.product_management.product;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import jakarta.transaction.Transactional;

@Transactional
public interface ProductRepository extends JpaRepository<ProductEntity, UUID>{
    @Modifying
    @Query(value = "INSERT INTO products (name, description, price, stock, img_location) VALUES (?1, ?2, ?3, ?4, ?5)", nativeQuery = true)
    void addProduct(String name, String description, int price, int stock, String imgLocation);

    Boolean existsByName(String name);

    @Query(value = "SELECT * FROM products WHERE product_id = ?1", nativeQuery = true)
    ProductEntity findByproductId(UUID productId);

    @Query(value = "SELECT * FROM products LIMIT ?1 OFFSET ?2", nativeQuery = true)
    List<ProductEntity> getProducts(int howMany, int page);
    
    @Modifying
    @Query(value = "UPDATE products SET stock = stock + ?2 WHERE product_id = ?1", nativeQuery = true)
    void setStock(UUID productId, int stockChange);

    @Modifying
    void deleteByName(String name);

    @Query(value = "SELECT * FROM products WHERE name LIKE %:keyword% OR description LIKE %:keyword% LIMIT :howMany OFFSET :page", nativeQuery = true)
    List<ProductEntity> searchProducts(String keyword, int howMany, int page);
}