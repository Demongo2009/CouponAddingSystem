package com.jgalat.coupon.springboot.service.geoIP.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeoIPServiceImplTest {

    @Mock
    private WebClient webClient;
    @Mock
    @SuppressWarnings("rawtypes")
    private RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    @SuppressWarnings("rawtypes")
    private RequestHeadersSpec requestHeadersSpec;
    @Mock
    private ResponseSpec responseSpec;

    @InjectMocks
    private GeoIPServiceImpl geoIPService;


    private final String IP_ADDRESS = "1.2.3.4";
    private final String COUNTRY = "Poland";
    private final String COUNTRY_KEY = "country";

    @Test
    void getCountryByIpAddress_shouldReturnCountry_whenValidResponse() {
        
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put(COUNTRY_KEY, COUNTRY);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(String.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(eq(Map.class))).thenReturn(Mono.just(responseMap));

        Optional<String> result = geoIPService.getCountryByIpAddress(IP_ADDRESS);
        assertThat(result).contains(COUNTRY);
    }

    @Test
    void getCountryByIpAddress_shouldReturnEmpty_whenCountryValueIsNull() {
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put(COUNTRY_KEY, null);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(String.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(eq(Map.class))).thenReturn(Mono.just(responseMap));

        Optional<String> result = geoIPService.getCountryByIpAddress(IP_ADDRESS);
        assertThat(result).isEmpty();
    }

    @Test
    void getCountryByIpAddress_shouldReturnEmpty_whenExceptionThrown() {

        doThrow(new RuntimeException("Connection error")).when(webClient).get();

        Optional<String> result = geoIPService.getCountryByIpAddress(IP_ADDRESS);   
        assertThat(result).isEmpty();
    }
} 