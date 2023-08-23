package com.order_management.order_management.order;

import java.util.LinkedList;
import java.util.List;
// import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
// import org.springframework.web.reactive.function.client.WebClient;

// import com.fasterxml.jackson.core.JsonProcessingException;
import com.order_management.order_management.order.api.OrderApis;
import com.order_management.order_management.order.dto.*;
import com.order_management.order_management.order.dto.UserOrderDto.DetailedOrderedItem;
import com.order_management.order_management.order.dto.UserOrderDto.OrderedProductDto;
import com.order_management.order_management.order.dto.UserOrderDto.UserOrderDto;
import com.order_management.order_management.order.entity.OrderEntity;
import com.order_management.order_management.order.entity.OrderedItemEntity;
import com.order_management.order_management.order.repository.OrderRepository;
import com.order_management.order_management.order.repository.OrderedItemRepository;
import com.order_management.order_management.order.saga.SagaOrchestrator;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderService {
    final OrderApis orderApis;
    final OrderRepository orderRepository;
    final OrderedItemRepository orderedItemRepository;
    final SagaOrchestrator sagaOrchestrator;

    public ResponseEntity<?> makeOrder(String accessToken, OrderDto orderDto) {
        // Get user info
        ResponseEntity<?> response = orderApis.getUserInfo(accessToken);
        if (response.getStatusCode().value() != 200) {
            return response;
        }
        UserInfoDto userInfoDto = (UserInfoDto) response.getBody();

        // Make order
        String sagaResult = sagaOrchestrator.makeOrder(orderDto, userInfoDto.getUserId());
        // Error in the saga
        if(!sagaResult.equals("OK")){
            return ResponseEntity.internalServerError().body(sagaResult);
        }

        // Send email
        EmailDto emailDto = EmailDto.builder()
            .to(userInfoDto.getEmail())
            .subject("Order placed")
            .text("Dear "+userInfoDto.getUsername()+"\nYour order has been placed.\nThank you for shopping with us.")
            .build();
        orderApis.sendEmail(emailDto);
        return ResponseEntity.ok("OK");
    }

    public ResponseEntity<?> getUserOrders(String accessToken) {
        // Get user info
        ResponseEntity<?> response = orderApis.getUserInfo(accessToken);
        if (response.getStatusCode().value() != 200) {
            return response;
        }
        UserInfoDto userInfoDto = (UserInfoDto) response.getBody();

        // Get user orders
        List<OrderEntity> orders = orderRepository.findByUserId(userInfoDto.getUserId());
        List<UserOrderDto> userOrders = new LinkedList<>(); 
        for(var order: orders){
            // Distributed JOIN of orderedItems and orderedProducts
            List<OrderedItemEntity> orderedItems = orderedItemRepository.findAllByOrderId(order.getOrderId());
            List<DetailedOrderedItem> detailedOrderedItems = new LinkedList<>();
            for(int i=0; i<orderedItems.size(); i++){
                // Get product info
                response = orderApis.getProduct(orderedItems.get(i).getId().getProductId());
                // if (response.getStatusCode().value() != 200) {
                //     return response;
                // }
                ProductDto productDto = (ProductDto) response.getBody();
                OrderedProductDto orderedProductDto = OrderedProductDto.builder()
                    .name(productDto.getName())
                    .description(productDto.getDescription())
                    .price(productDto.getPrice())
                    .imgLocation(productDto.getImgLocation())
                    .build();
                detailedOrderedItems.add(DetailedOrderedItem.builder()
                    .orderedProduct(orderedProductDto)
                    .quantity(orderedItems.get(i).getQuantity())
                    .build()
                );
            }
            // Add a userOrder using the query results
            userOrders.add(UserOrderDto.builder()
                .orderId(order.getOrderId())
                .orderDate(order.getOrderDate())
                .detailedOrderedItems(detailedOrderedItems)
                .build()
            );
        }
        return ResponseEntity.ok(userOrders);
    }
}
