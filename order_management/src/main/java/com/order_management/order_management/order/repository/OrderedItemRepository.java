package com.order_management.order_management.order.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.order_management.order_management.order.entity.OrderedItemEntity;

import jakarta.transaction.Transactional;

@Transactional
public interface OrderedItemRepository extends JpaRepository<OrderedItemEntity, UUID>{
    @Modifying
    @Query(value="INSERT INTO ordered_items (order_id, product_id, quantity) VALUES (?1, ?2, ?3);", nativeQuery = true)
    void addOrderedItem(UUID orderId, UUID productId, int quantity);

    @Query(value="SELECT * FROM ordered_items WHERE order_id = ?;", nativeQuery = true)
    List<OrderedItemEntity> findAllByOrderId(UUID orderId);

    @Modifying
    @Query(value="DELETE FROM ordered_items WHERE order_id = ?;", nativeQuery = true)
    void deleteAllByOrderId(UUID orderId);
}
