package com.order_management.order_management.order.api;

import java.util.UUID;
// import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.order_management.order_management.order.dto.EmailDto;
import com.order_management.order_management.order.dto.ProductDto;
import com.order_management.order_management.order.dto.UserInfoDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OrderApis {
    @Value("${gateway.url}") String gatewayUrl;
    final KafkaTemplate<String, String> kafkaTemplate;

    public ResponseEntity<?> getUserInfo(String accessToken){
        // int httpStatusCode = 200;
        try{
            return WebClient.create()
                .get().uri(gatewayUrl+"/userManagement/getUserInfo")
                .header("accessToken", accessToken)
                .retrieve()
                .toEntity(UserInfoDto.class)
                // .bodyToMono(UserInfoDto.class)
                .block();
        } catch(WebClientResponseException e){
            // httpStatusCode = e.getStatusCode().value();
            HttpStatusCode status = e.getStatusCode();
            String errorMessage = e.getResponseBodyAsString();
            return ResponseEntity.status(status).body(errorMessage);
        }

        // if(httpStatusCode == 200){
        //     return ResponseEntity.ok().body(response);
        // } else {
        //     ResponseEntity.internalServerError().body("Internal Server Error");
        // }
    }

    public ResponseEntity<?> getProduct(UUID productId){
        try{
            return WebClient.create()
                .get().uri(gatewayUrl+"/productManagement/getProduct/"+productId)
                .retrieve()
                .toEntity(ProductDto.class)
                .block();
        } catch(WebClientResponseException e){
            HttpStatusCode status = e.getStatusCode();
            String errorMessage = e.getResponseBodyAsString();
            return ResponseEntity.status(status).body(errorMessage);
        }
    }

    public ResponseEntity<String> setStock(UUID productId, int quantity){
        try{
            return WebClient.create()
                .patch().uri(gatewayUrl+"/productManagement/manager/setStock/"+productId+"/"+quantity)
                .header("password", "123")
                .retrieve()
                .toEntity(String.class)
                .block();
        } catch(WebClientResponseException e){
            HttpStatusCode status = e.getStatusCode();
            String errorMessage = e.getResponseBodyAsString();
            return ResponseEntity.status(status).body(errorMessage);
        }
    }

    public void sendEmail(EmailDto emailDto) {
        String emailJson;
        try {
            emailJson = new ObjectMapper().writeValueAsString(emailDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON format exception", e);
        }
        kafkaTemplate.send("email", emailJson);
    }
}