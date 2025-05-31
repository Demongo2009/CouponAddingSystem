package com.jgalat.coupon.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import com.jgalat.coupon.springboot.model.Coupon;

import jakarta.persistence.LockModeType;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    Coupon findByCode(String code);

}
