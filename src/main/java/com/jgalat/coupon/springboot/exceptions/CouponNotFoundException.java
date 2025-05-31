package com.jgalat.coupon.springboot.exceptions;

public class CouponNotFoundException extends Exception {

    private static final String DEFAULT_MESSAGE = "Coupon not found";

    public CouponNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public CouponNotFoundException(String message) {
        super(message);
    }

    public CouponNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
