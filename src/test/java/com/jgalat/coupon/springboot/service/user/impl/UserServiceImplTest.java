package com.jgalat.coupon.springboot.service.user.impl;

import com.jgalat.coupon.springboot.model.User;
import com.jgalat.coupon.springboot.model.payload.RegistrationRequest;
import com.jgalat.coupon.springboot.model.payload.RegistrationResponse;
import com.jgalat.coupon.springboot.repository.UserRepository;
import com.jgalat.coupon.springboot.service.geoIP.GeoIPService;
import com.jgalat.coupon.springboot.utils.GeneralMessageAccessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private GeoIPService geoIPService;
    @Mock
    private GeneralMessageAccessor generalMessageAccessor;

    @InjectMocks
    private UserServiceImpl userService;

    private RegistrationRequest registrationRequest;
    private User user;

    private final String USER_USERNAME = "testuser";
    private final String USER_COUNTRY = "Poland";
    private final String USER_UNKNOWN_COUNTRY = "Unknown";
    private final String REGISTRATION_SUCCESSFUL_MESSAGE = "Registration successful";
    private final String USER_PASSWORD = "password";
    private final String ENCODED_PASSWORD = "encodedPassword";
    private final String IP_ADDRESS = "1.2.3.4";

    @BeforeEach
    void setUp() {
        registrationRequest = new RegistrationRequest(USER_USERNAME, USER_USERNAME, USER_PASSWORD);
        user = User.builder()
                .username(USER_USERNAME)
                .password(ENCODED_PASSWORD)
                .country(USER_COUNTRY)
                .build();
    }

    @Test
    void findByUsername_shouldReturnUser() {
        when(userRepository.findByUsername(USER_USERNAME)).thenReturn(user);

        User result = userService.findByUsername(USER_USERNAME);
        assertThat(result).isEqualTo(user);
    }

    @Test
    void registration_shouldSaveUserWithCountryFromGeoIP() {
        when(geoIPService.getCountryByIpAddress(IP_ADDRESS)).thenReturn(Optional.of(USER_COUNTRY));
        when(bCryptPasswordEncoder.encode(USER_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(generalMessageAccessor.getMessage(isNull(), eq("registration_successful"), eq(USER_USERNAME)))
                .thenReturn(REGISTRATION_SUCCESSFUL_MESSAGE);

        RegistrationResponse response = userService.registration(registrationRequest, IP_ADDRESS);
        verify(userRepository).save(argThat(u -> u.getCountry().equals(USER_COUNTRY)));
        assertThat(response.getMessage()).isEqualTo(REGISTRATION_SUCCESSFUL_MESSAGE);
    }

    @Test
    void registration_shouldSaveUserWithUnknownCountryIfGeoIPEmpty() {
        when(geoIPService.getCountryByIpAddress(IP_ADDRESS)).thenReturn(Optional.empty());
        when(bCryptPasswordEncoder.encode(USER_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(generalMessageAccessor.getMessage(isNull(), eq("registration_successful"), eq(USER_USERNAME)))
                .thenReturn(REGISTRATION_SUCCESSFUL_MESSAGE);

        RegistrationResponse response = userService.registration(registrationRequest, IP_ADDRESS);
        verify(userRepository).save(argThat(u -> u.getCountry().equals(USER_UNKNOWN_COUNTRY)));
        assertThat(response.getMessage()).isEqualTo(REGISTRATION_SUCCESSFUL_MESSAGE);
    }

    @Test
    void saveUser_shouldSaveUser() {

        userService.saveUser(user);
        verify(userRepository).save(user);
    }
} 