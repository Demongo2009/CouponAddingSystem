package com.jgalat.coupon.springboot.model.payload;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;

@Data
public class RegistrationRequest {

	@NotEmpty(message = "{registration_name_not_empty}")
	private final String name;

	@NotEmpty(message = "{registration_username_not_empty}")
	private final String username;

	@NotEmpty(message = "{registration_password_not_empty}")
	private final String password;

}
