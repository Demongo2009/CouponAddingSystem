package com.jgalat.coupon.springboot.exceptions;

public class CouponNotValidForUserCountryException extends Exception {

    private static final String DEFAULT_MESSAGE = "Coupon not valid for user country";

    public CouponNotValidForUserCountryException() {
        super(DEFAULT_MESSAGE);
    }

    public CouponNotValidForUserCountryException(String message) {
        super(message);
    }

    public CouponNotValidForUserCountryException(String message, Throwable cause) {
        super(message, cause);
    }
}
