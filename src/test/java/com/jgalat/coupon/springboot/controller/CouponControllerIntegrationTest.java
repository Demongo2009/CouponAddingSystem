package com.jgalat.coupon.springboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jgalat.coupon.springboot.model.payload.AddCouponRequest;
import com.jgalat.coupon.springboot.model.payload.AddCouponResponse;
import com.jgalat.coupon.springboot.model.payload.UseCouponRequest;
import com.jgalat.coupon.springboot.model.payload.UseCouponResponse;
import com.jgalat.coupon.springboot.service.coupon.CouponService;
import com.jgalat.coupon.springboot.SpringBootCouponApplication;
import com.jgalat.coupon.springboot.exceptions.CouponNotFoundException;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


@SpringBootTest(classes = SpringBootCouponApplication.class)
@AutoConfigureMockMvc
@Testcontainers
public class CouponControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CouponService couponService;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.driver-class-name", mysql::getDriverClassName);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.MySQL8Dialect");
    }

    private final String COUPON_CODE = "TESTCODE";
    private final int COUPON_MAX_USAGES = 5;
    private final String COUPON_DESTINED_COUNTRY = "Poland";
    private final String USER_USERNAME = "testuser";
    private final String COUPON_NOT_FOUND_CODE = "notfound";
    private final String USE_COUPON_URL = "/api/v1/coupons/use";
    private final String ADD_COUPON_URL = "/api/v1/coupons/add";

    @Test
    public void addCoupon_shouldReturnSuccess() throws Exception {
        AddCouponRequest request = new AddCouponRequest(COUPON_CODE, COUPON_MAX_USAGES, COUPON_DESTINED_COUNTRY);
        Mockito.when(couponService.saveCoupon(any())).thenReturn(
                new AddCouponResponse("coupon_added_successfully")
        );

        mockMvc.perform(post(ADD_COUPON_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("coupon_added_successfully"));
    }

    @Test
    public void addCoupon_shouldReturnValidationError() throws Exception {
        AddCouponRequest request = new AddCouponRequest("", COUPON_MAX_USAGES, COUPON_DESTINED_COUNTRY);

        mockMvc.perform(post(ADD_COUPON_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void useCoupon_shouldReturnSuccess() throws Exception {
        UseCouponRequest request = new UseCouponRequest(COUPON_CODE, USER_USERNAME);
        Mockito.when(couponService.useCoupon(anyString(), anyString()))
                .thenReturn(new UseCouponResponse("coupon_used_successfully"));

        mockMvc.perform(post(USE_COUPON_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("coupon_used_successfully"));
    }

    @Test
    public void useCoupon_shouldReturnError() throws Exception {
        UseCouponRequest request = new UseCouponRequest(COUPON_NOT_FOUND_CODE, USER_USERNAME);
        Mockito.when(couponService.useCoupon(anyString(), anyString()))
                .thenThrow(new CouponNotFoundException("coupon_not_found"));

        mockMvc.perform(post(USE_COUPON_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }
} 