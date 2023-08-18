package com.order_management.order_management.order.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.order_management.order_management.order.entity.OrderEntity;

import jakarta.transaction.Transactional;

@Transactional
public interface OrderRepository extends JpaRepository<OrderEntity, UUID>{
    @Modifying
    @Query(value = "INSERT INTO orders (order_id, user_id) VALUES (?1, ?2)", nativeQuery = true)
    void addOrder(UUID orderId, UUID userId);
    
    // @Query(value = "SELECT * FROM orders WHERE order_id = ?;", nativeQuery = true)
    // OrderEntity findByOrderId(UUID orderId);

    @Query(value="SELECT * FROM orders A WHERE user_id = ?;", nativeQuery = true)
    List<OrderEntity> findByUserId(UUID userId);

    // Check if the order belongs to the user before cancelling order
    @Query(value = "SELECT EXISTS(SELECT 1 FROM orders WHERE order_id = ? AND user_id = ?);", nativeQuery = true)
    int checkOrderBelongsToUser(UUID orderId, UUID userId);

    // @Modifying
    // @Query(value = "DELETE FROM orders WHERE order_id = ?;", nativeQuery = true)
    // void deleteByOrderId(UUID orderId);
}
