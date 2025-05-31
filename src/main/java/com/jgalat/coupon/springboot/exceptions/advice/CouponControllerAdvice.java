package com.jgalat.coupon.springboot.exceptions.advice;

import com.jgalat.coupon.springboot.controller.CouponController;
import com.jgalat.coupon.springboot.exceptions.ApiExceptionResponse;
import com.jgalat.coupon.springboot.exceptions.CouponMaxUsagesExceededException;
import com.jgalat.coupon.springboot.exceptions.CouponAlreadyUsedException;
import com.jgalat.coupon.springboot.exceptions.CouponNotValidForUserCountryException;
import com.jgalat.coupon.springboot.exceptions.UserNotFoundException;
import com.jgalat.coupon.springboot.exceptions.CouponMaxUsagesRetriesExceededException;
import com.jgalat.coupon.springboot.exceptions.CouponNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice(basePackageClasses = CouponController.class)
public class CouponControllerAdvice {

	@ExceptionHandler(CouponMaxUsagesExceededException.class)
	ResponseEntity<ApiExceptionResponse> handleCouponMaxUsagesExceededException(CouponMaxUsagesExceededException exception) {

		final ApiExceptionResponse response = new ApiExceptionResponse(exception.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());

		return ResponseEntity.status(response.getStatus()).body(response);
	}

	@ExceptionHandler(CouponAlreadyUsedException.class)
	ResponseEntity<ApiExceptionResponse> handleCouponAlreadyUsedException(CouponAlreadyUsedException exception) {

		final ApiExceptionResponse response = new ApiExceptionResponse(exception.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());

		return ResponseEntity.status(response.getStatus()).body(response);
	}

	@ExceptionHandler(CouponNotValidForUserCountryException.class)
	ResponseEntity<ApiExceptionResponse> handleCouponNotValidForUserCountryException(CouponNotValidForUserCountryException exception) {

		final ApiExceptionResponse response = new ApiExceptionResponse(exception.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());

		return ResponseEntity.status(response.getStatus()).body(response);
	}

	@ExceptionHandler(CouponMaxUsagesRetriesExceededException.class)
	ResponseEntity<ApiExceptionResponse> handleCouponMaxUsagesRetriesExceededException(CouponMaxUsagesRetriesExceededException exception) {

		final ApiExceptionResponse response = new ApiExceptionResponse(exception.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());

		return ResponseEntity.status(response.getStatus()).body(response);
	}

	@ExceptionHandler(CouponNotFoundException.class)
	ResponseEntity<ApiExceptionResponse> handleCouponNotFoundException(CouponNotFoundException exception) {

		final ApiExceptionResponse response = new ApiExceptionResponse(exception.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());

		return ResponseEntity.status(response.getStatus()).body(response);
	}

	@ExceptionHandler(UserNotFoundException.class)
	ResponseEntity<ApiExceptionResponse> handleUserNotFoundException(UserNotFoundException exception) {

		final ApiExceptionResponse response = new ApiExceptionResponse(exception.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());

		return ResponseEntity.status(response.getStatus()).body(response);
	}




}
