package com.jgalat.coupon.springboot.exceptions;

public class CouponAlreadyUsedException extends Exception {

    private static final String DEFAULT_MESSAGE = "Coupon already used";

    public CouponAlreadyUsedException() {
        super(DEFAULT_MESSAGE);
    }

    public CouponAlreadyUsedException(String message) {
        super(message);
    }

    public CouponAlreadyUsedException(String message, Throwable cause) {
        super(message, cause);
    }
}
