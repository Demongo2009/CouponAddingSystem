package com.jgalat.coupon.springboot.service.user;

import com.jgalat.coupon.springboot.model.User;
import com.jgalat.coupon.springboot.model.payload.RegistrationRequest;
import com.jgalat.coupon.springboot.model.payload.RegistrationResponse;

public interface UserService {

	User findByUsername(String username);

	RegistrationResponse registration(RegistrationRequest registrationRequest, String ipAddress);

    void saveUser(User user);

}
