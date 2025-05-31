package com.jgalat.coupon.springboot.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jgalat.coupon.springboot.model.payload.RegistrationRequest;
import com.jgalat.coupon.springboot.model.payload.RegistrationResponse;
import com.jgalat.coupon.springboot.service.user.UserService;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/register")
public class RegistrationController {

	private final UserService userService;

	@PostMapping
	public ResponseEntity<RegistrationResponse> registrationRequest(@Valid @RequestBody RegistrationRequest registrationRequest, 
	  @RequestHeader("X-Forwarded-For") String ipAddress) { 	

		final RegistrationResponse registrationResponse = userService.registration(registrationRequest, ipAddress);

		log.info("Succeesful call to registrationRequest");
		return ResponseEntity.status(HttpStatus.CREATED).body(registrationResponse);
	}

}
