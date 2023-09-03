package com.product_management.product_management;

import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.product_management.product_management.product.ProductEntity;
import com.product_management.product_management.product.ProductRepository;
import com.product_management.product_management.product.dto_.NewProductDto;
import com.product_management.product_management.product.saga_.SagaOrchestrator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
public class ApiTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean ProductRepository productRepository;
    @MockBean SagaOrchestrator sagaOrchestrator;
    @MockBean AmazonS3 amazonS3;

    @Test
    void addProductTest_successful() throws Exception {
        NewProductDto newProductDto = new NewProductDto("testProduct", "testDescription", 100, 100, "testImg");
        when(productRepository.existsByName(any())).thenReturn(null);
        when(sagaOrchestrator.addProduct(any(), any(), any())).thenReturn("OK");
        mockMvc.perform(post("/manager/addProduct")
                .header("password", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProductDto)))
                .andExpect(status().isOk());
    }

    @Test
    void addProductTest_productNameExists() throws Exception {
        NewProductDto newProductDto = new NewProductDto("testProduct", "testDescription", 100, 100, "testImg");
        when(productRepository.existsByName(any())).thenReturn(new ProductEntity());
        mockMvc.perform(post("/manager/addProduct")
                .header("password", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProductDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("PRODUCT_NAME_EXISTS"));
    }

    @Test
    void addProductTest_sagaFailed() throws Exception {
        NewProductDto newProductDto = new NewProductDto("testProduct", "testDescription", 100, 100, "testImg");
        when(productRepository.existsByName(any())).thenReturn(null);
        when(sagaOrchestrator.addProduct(any(), any(), any())).thenReturn("SAGA_FAILED");
        mockMvc.perform(post("/manager/addProduct")
                .header("password", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProductDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("SAGA_FAILED"));
    }

    @Test
    void getProductsTest_successful() throws Exception {
        when(productRepository.getProducts(anyInt(), anyInt())).thenReturn(new ArrayList<ProductEntity>());
        mockMvc.perform(get("/getProducts/10/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getProductTest_successful() throws Exception {
        UUID uuid = UUID.randomUUID();
        when(productRepository.findByProductId(any())).thenReturn(new ProductEntity());
        mockMvc.perform(get("/getProduct/"+uuid))
                .andExpect(status().isOk());
    }
    @Test
    void getProductTest_productNotFound() throws Exception {
        UUID uuid = UUID.randomUUID();
        when(productRepository.findByProductId(any())).thenReturn(null);
        mockMvc.perform(get("/getProduct/"+uuid))
                .andExpect(status().isNotFound())
                .andExpect(content().string("PRODUCT_NOT_FOUND"));
    }
    @Test
    void setStockTest_successful() throws Exception {
        UUID uuid = UUID.randomUUID();
        when(productRepository.existsByProductId(any())).thenReturn(new ProductEntity());
        mockMvc.perform(patch("/manager/setStock/"+uuid+"/10")
                .header("password", "123"))
                .andExpect(status().isOk());
    }
    @Test
    void setStockTest_productNotFound() throws Exception {
        UUID uuid = UUID.randomUUID();
        when(productRepository.existsByProductId(any())).thenReturn(null);
        mockMvc.perform(patch("/manager/setStock/"+uuid+"/10")
                .header("password", "123"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("PRODUCT_NOT_FOUND"));
    }
    @Test
    void setStockTest_dbError() throws Exception {
        UUID uuid = UUID.randomUUID();
        when(productRepository.existsByProductId(any())).thenReturn(new ProductEntity());
        doThrow(new RuntimeException()).when(productRepository).setStock(any(), anyInt());
        mockMvc.perform(patch("/manager/setStock/"+uuid+"/10")
                .header("password", "123"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("DB_ERROR"));
    }
    @Test
    void deleteProductTest_successful() throws Exception {
        UUID uuid = UUID.randomUUID();
        when(productRepository.findByProductId(any())).thenReturn(new ProductEntity());
        mockMvc.perform(delete("/manager/deleteProduct/"+uuid)
                .header("password", "123"))
                .andExpect(status().isOk());
    }
    @Test
    void deleteProductTest_productNotFound() throws Exception {
        UUID uuid = UUID.randomUUID();
        when(productRepository.findByProductId(any())).thenReturn(null);
        mockMvc.perform(delete("/manager/deleteProduct/"+uuid)
                .header("password", "123"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("PRODUCT_NOT_FOUND"));
    }
    @Test
    void searchProductsTest_successful() throws Exception {
        when(productRepository.searchProducts(anyString(), anyInt(), anyInt())).thenReturn(new ArrayList<ProductEntity>());
        mockMvc.perform(get("/searchProducts/test/10/1"))
                .andExpect(status().isOk());
    }
}
