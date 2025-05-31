package com.jgalat.coupon.springboot.exceptions;

public class CouponMaxUsagesRetriesExceededException extends Exception {

    private static final String DEFAULT_MESSAGE = "Coupon max usages retries exceeded";

    public CouponMaxUsagesRetriesExceededException() {
        super(DEFAULT_MESSAGE);
    }

    public CouponMaxUsagesRetriesExceededException(String message) {
        super(message);
    }

    public CouponMaxUsagesRetriesExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}
