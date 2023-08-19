package com.order_management.order_management.order.saga;

import java.util.Deque;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
// import org.springframework.web.reactive.function.client.WebClient;

import com.order_management.order_management.order.api.OrderApis;
import com.order_management.order_management.order.dto.OrderDto;
import com.order_management.order_management.order.repository.OrderRepository;
import com.order_management.order_management.order.repository.OrderedItemRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SagaOrchestrator {
    final OrderRepository orderRepository;
    final OrderedItemRepository orderedItemRepository;
    final TransactionTemplate transactionTemplate;
    final OrderApis orderApis;

    public String makeOrder(OrderDto orderDto, UUID userId){
        // Make payment
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("Making payment");
            return "OK";
        });

        // Subtract stock
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            ResponseEntity<String> response = ResponseEntity.internalServerError().body("Uncaught error");
            for(var orderedItem: orderDto.getOrderedItems()) {
                response = orderApis.setStock(orderedItem.getProductId(), -orderedItem.getQuantity());
                if(response.getStatusCode().value() != 200)
                    break;
            }
            return response.getBody();
        });

        // Add order and ordered items
        UUID orderId = UUID.randomUUID();
        CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> {
            try {
                transactionTemplate.execute(status -> {
                    orderRepository.addOrder(orderId, userId);
                    orderDto.getOrderedItems().forEach(orderedItem -> {
                        orderedItemRepository.addOrderedItem(orderId, orderedItem.getProductId(), orderedItem.getQuantity());
                    });
                    return null;
                });
                return "OK";
            } catch (Exception e) {
                return e.getMessage();
            }
        });

        // Join futures and prepare for compensations
        Deque<Runnable> rollbacks = new LinkedList<>();
        Boolean success = true;
        String errMsg = "";
        String future1Result = future1.join();

        
        if(future1Result.equals("OK")){
            rollbacks.push(() -> {
                System.out.println("Rolling back payment");
            });
        } else {
            success = false;
            errMsg += future1Result+"\n";
        }

        String future2Result = future2.join();
        if(future2Result.equals("OK")){
            rollbacks.push(() -> {
                orderDto.getOrderedItems().forEach(orderedItem -> {
                    orderApis.setStock(orderedItem.getProductId(), orderedItem.getQuantity());
                });
            });
        } else {
            success = false;
            errMsg += future2Result+"\n";
        }

        String future3Result = future3.join();
        if(future3Result.equals("OK")){
            rollbacks.push(() -> {
                transactionTemplate.execute(status -> {
                    orderRepository.deleteById(orderId);
                    orderedItemRepository.deleteAllByOrderId(orderId);
                    return null;
                });
            });
        } else {
            success = false;
            errMsg += future3Result+"\n";
        }

        // Complete the transaction or perform compensation if necessary
        if(success) {
            return "OK";
        }
        else {
            while(!rollbacks.isEmpty())
                rollbacks.pop().run();
            return errMsg;
        }
    }
}
