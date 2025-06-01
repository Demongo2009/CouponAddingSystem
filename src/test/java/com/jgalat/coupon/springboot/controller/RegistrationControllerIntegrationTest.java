package com.jgalat.coupon.springboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jgalat.coupon.springboot.model.payload.RegistrationRequest;
import com.jgalat.coupon.springboot.model.payload.RegistrationResponse;
import com.jgalat.coupon.springboot.service.user.UserService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class RegistrationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

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

    private final String REGISTRATION_URL = "/api/v1/register";
    private final String USER_NAME = "Test Name";
    private final String USER_USERNAME = "testuser";
    private final String USER_PASSWORD = "password";
    private final String IP_ADDRESS = "1.2.3.4";

    @Test
    public void registration_shouldReturnCreated() throws Exception {
        RegistrationRequest request = new RegistrationRequest(USER_NAME, USER_USERNAME, USER_PASSWORD);
        
        Mockito.when(userService.registration(any(RegistrationRequest.class), any(String.class)))
                .thenReturn(new RegistrationResponse("registration_successful"));

        mockMvc.perform(post(REGISTRATION_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Forwarded-For", IP_ADDRESS)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("registration_successful"));
    }

    @Test
    public void registration_shouldReturnValidationError() throws Exception {
        RegistrationRequest request = new RegistrationRequest(USER_NAME, "", USER_PASSWORD);

        mockMvc.perform(post(REGISTRATION_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Forwarded-For", IP_ADDRESS)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
} 