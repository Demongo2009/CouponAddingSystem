package com.jgalat.coupon.springboot.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RegistrationException extends Exception {

	private final String errorMessage;

}
