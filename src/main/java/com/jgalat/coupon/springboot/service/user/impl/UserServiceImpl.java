package com.jgalat.coupon.springboot.service.user.impl;

import com.jgalat.coupon.springboot.model.User;
import com.jgalat.coupon.springboot.model.payload.RegistrationRequest;
import com.jgalat.coupon.springboot.model.payload.RegistrationResponse;
import com.jgalat.coupon.springboot.repository.UserRepository;
import com.jgalat.coupon.springboot.service.geoIP.GeoIPService;
import com.jgalat.coupon.springboot.service.user.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicReference;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.jgalat.coupon.springboot.utils.GeneralMessageAccessor;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private static final String REGISTRATION_SUCCESSFUL = "registration_successful";

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final GeoIPService geoIPService;
	private final GeneralMessageAccessor generalMessageAccessor;
	@Override
	public User findByUsername(String username) {

		return userRepository.findByUsername(username);
	}

	@Override
	public RegistrationResponse registration(RegistrationRequest registrationRequest, String ipAddress) {

		AtomicReference<String> country = new AtomicReference<>("Unknown");

		log.info("Getting country by ip address: {}", ipAddress);
		geoIPService.getCountryByIpAddress(ipAddress).ifPresentOrElse(country::set, () -> {
			log.error("Country not found by ip address: {}", ipAddress);
		});

		final User user = User.builder()
			.username(registrationRequest.getUsername())
			.password(bCryptPasswordEncoder.encode(registrationRequest.getPassword()))
			.country(country.get())
			.build();

		log.info("Saving user: {}", user);
		userRepository.save(user);

		log.info("{} registered successfully!", registrationRequest.getUsername());
		return new RegistrationResponse(generalMessageAccessor.getMessage(null, REGISTRATION_SUCCESSFUL, registrationRequest.getUsername()));
	}

	@Override
	public void saveUser(User user) {
		log.info("Saving user: {}", user);
		userRepository.save(user);
	}
}
