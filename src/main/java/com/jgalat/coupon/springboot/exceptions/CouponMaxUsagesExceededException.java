package com.jgalat.coupon.springboot.exceptions;

public class CouponMaxUsagesExceededException extends Exception {

    private static final String DEFAULT_MESSAGE = "Coupon max usages exceeded";

    public CouponMaxUsagesExceededException() {
        super(DEFAULT_MESSAGE);
    }

    public CouponMaxUsagesExceededException(String message) {
        super(message);
    }

    public CouponMaxUsagesExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}
