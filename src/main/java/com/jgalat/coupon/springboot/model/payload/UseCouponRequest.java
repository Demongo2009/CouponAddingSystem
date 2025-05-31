package com.jgalat.coupon.springboot.model.payload;

import jakarta.validation.constraints.Pattern;

import org.springframework.lang.NonNull;

import lombok.Data;

@Data
public class UseCouponRequest {

    @Pattern(regexp = "^[A-Za-z]+", message = "{coupon_coupon_code_alpha_only}")
    @NonNull
    private final String couponCode;
    
    @NonNull
    private final String userName;
}