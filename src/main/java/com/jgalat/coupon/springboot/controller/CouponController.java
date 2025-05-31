package com.jgalat.coupon.springboot.controller;

import com.jgalat.coupon.springboot.configuration.aspects.TrackExecutionTime;
import com.jgalat.coupon.springboot.exceptions.CouponAlreadyUsedException;
import com.jgalat.coupon.springboot.exceptions.CouponMaxUsagesExceededException;
import com.jgalat.coupon.springboot.exceptions.CouponMaxUsagesRetriesExceededException;
import com.jgalat.coupon.springboot.exceptions.CouponNotFoundException;
import com.jgalat.coupon.springboot.exceptions.CouponNotValidForUserCountryException;
import com.jgalat.coupon.springboot.exceptions.UserNotFoundException;
import com.jgalat.coupon.springboot.model.Coupon;
import com.jgalat.coupon.springboot.model.payload.AddCouponRequest;
import com.jgalat.coupon.springboot.model.payload.AddCouponResponse;
import com.jgalat.coupon.springboot.model.payload.UseCouponRequest;
import com.jgalat.coupon.springboot.model.payload.UseCouponResponse;
import com.jgalat.coupon.springboot.service.coupon.CouponService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/coupons")
public class CouponController {

    private final CouponService couponService;

    @PostMapping("/add")
    @TrackExecutionTime
    public ResponseEntity<AddCouponResponse> addCoupon(@RequestBody @Valid AddCouponRequest coupon) {
        AddCouponResponse savedCoupon = couponService.saveCoupon(coupon);

        log.info("Succeesful call to addCoupon");
        return ResponseEntity.ok(savedCoupon);
    }

    @PostMapping("/use")
    @TrackExecutionTime
    public ResponseEntity<UseCouponResponse> useCoupon(@RequestBody @Valid UseCouponRequest useCouponPayload) throws CouponMaxUsagesExceededException, CouponAlreadyUsedException, CouponNotValidForUserCountryException, UserNotFoundException, CouponNotFoundException, CouponMaxUsagesRetriesExceededException {
        UseCouponResponse useCouponResponse = couponService.useCoupon(useCouponPayload.getCouponCode(), useCouponPayload.getUserName());

        log.info("Succeesful call to useCoupon");
        return ResponseEntity.ok(useCouponResponse);
    }
}
