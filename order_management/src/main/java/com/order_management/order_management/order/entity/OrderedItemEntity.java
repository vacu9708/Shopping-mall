package com.order_management.order_management.order.entity;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
// import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// doesn't work without @Id!
// @Getter
// @AllArgsConstructor
// @NoArgsConstructor
// @Entity(name = "ordered_items")
// public class OrderedItemEntity {
//     UUID orderId;
//     UUID productId;
//     int quantity;
// }

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "ordered_items")
public class OrderedItemEntity {
    @EmbeddedId
    OrderedItemId id;
    int quantity;

    @Embeddable
    @Getter
    public static class OrderedItemId implements Serializable {
        UUID orderId;
        UUID productId;
    }
}
