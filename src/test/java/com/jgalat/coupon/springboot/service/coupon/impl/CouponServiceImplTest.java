package com.jgalat.coupon.springboot.service.coupon.impl;

import com.jgalat.coupon.springboot.exceptions.*;
import com.jgalat.coupon.springboot.model.*;
import com.jgalat.coupon.springboot.model.payload.*;
import com.jgalat.coupon.springboot.repository.CouponRepository;
import com.jgalat.coupon.springboot.service.user.UserService;
import com.jgalat.coupon.springboot.utils.ExceptionMessageAccessor;
import com.jgalat.coupon.springboot.utils.GeneralMessageAccessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceImplTest {

    @Mock
    private CouponRepository couponRepository;
    @Mock
    private UserService userService;
    @Mock
    private GeneralMessageAccessor generalMessageAccessor;
    @Mock
    private ExceptionMessageAccessor exceptionMessageAccessor;

    @InjectMocks
    private CouponServiceImpl couponService;

    private Coupon coupon;
    private User user;
    private AddCouponRequest addCouponRequest;

    private final String COUPON_CODE = "testcoupon";
    private final int COUPON_MAX_USAGES = 5;
    private final String COUPON_DESTINED_COUNTRY = "Poland";
    private final String USER_USERNAME = "testuser";
    private final String USER_COUNTRY = "Poland";
    private final String USER_NOT_FOUND_USERNAME = "nouser";
    private final String COUPON_NOT_FOUND_CODE = "notfound";
    private final String COUPON_ADDED_SUCCESSFULLY_MESSAGE = "Coupon added successfully";
    private final String COUPON_USED_SUCCESSFULLY_MESSAGE = "Coupon used successfully";
    private final String COUPON_NOT_FOUND_MESSAGE = "Coupon not found";
    private final String COUPON_ALREADY_USED_MESSAGE = "Coupon already used";
    private final String COUPON_MAX_USAGES_EXCEEDED_MESSAGE = "Max usages exceeded";
    private final String COUPON_NOT_VALID_FOR_USER_COUNTRY_MESSAGE = "Not valid for user country";
    private final String USER_NOT_FOUND_MESSAGE = "User not found";

    @BeforeEach
    void setUp() {
        coupon = Coupon.builder()
                .id(1L)
                .code(COUPON_CODE)
                .maxUsages(COUPON_MAX_USAGES)
                .currentUsages(0)
                .destinedCountry(COUPON_DESTINED_COUNTRY)
                .usersUsingTheCoupon(new HashSet<>())
                .build();

        user = User.builder()
                .id(1L)
                .username(USER_USERNAME)
                .country(USER_COUNTRY)
                .usedCoupons(new HashSet<>())
                .build();

        addCouponRequest = new AddCouponRequest(COUPON_CODE, COUPON_MAX_USAGES, COUPON_DESTINED_COUNTRY);
    }

    @Test
    void saveCoupon_shouldSaveAndReturnResponse() {

        when(generalMessageAccessor.getMessage(any(), eq("coupon_added_successfully"), any(Coupon.class)))
                .thenReturn(COUPON_ADDED_SUCCESSFULLY_MESSAGE);

        AddCouponResponse response = couponService.saveCoupon(addCouponRequest);

        verify(couponRepository).save(any(Coupon.class));
        assertThat(response.getMessage()).isEqualTo(COUPON_ADDED_SUCCESSFULLY_MESSAGE);
    }

    @Test
    void useCoupon_shouldSucceed() throws Exception {
        when(couponRepository.findByCode(COUPON_CODE)).thenReturn(coupon);
        when(userService.findByUsername(USER_USERNAME)).thenReturn(user);
        when(generalMessageAccessor.getMessage(any(), eq("coupon_used_successfully"), any(Coupon.class)))
                .thenReturn(COUPON_USED_SUCCESSFULLY_MESSAGE);

        UseCouponResponse response = couponService.useCoupon(COUPON_CODE, USER_USERNAME);

        verify(userService).saveUser(user);
        verify(couponRepository).save(coupon);
        assertThat(response.getMessage()).isEqualTo(COUPON_USED_SUCCESSFULLY_MESSAGE);
        assertThat(user.getUsedCoupons()).contains(coupon);
        assertThat(coupon.getUsersUsingTheCoupon()).contains(user);
        assertThat(coupon.getCurrentUsages()).isEqualTo(1);
    }

    @Test
    void useCoupon_shouldThrowCouponNotFoundException() {
        when(couponRepository.findByCode(COUPON_NOT_FOUND_CODE)).thenReturn(null);
        when(exceptionMessageAccessor.getMessage(any(), eq("coupon_not_found"), any())).thenReturn(COUPON_NOT_FOUND_MESSAGE);

        assertThatThrownBy(() -> couponService.useCoupon(COUPON_NOT_FOUND_CODE, USER_USERNAME))
                .isInstanceOf(CouponNotFoundException.class)
                .hasMessageContaining(COUPON_NOT_FOUND_MESSAGE);
    }

    @Test
    void useCoupon_shouldThrowUserNotFoundException() {
        when(couponRepository.findByCode(COUPON_CODE)).thenReturn(coupon);
        when(userService.findByUsername(USER_NOT_FOUND_USERNAME)).thenReturn(null);
        when(exceptionMessageAccessor.getMessage(any(), eq("user_not_found"), any())).thenReturn(USER_NOT_FOUND_MESSAGE);

        assertThatThrownBy(() -> couponService.useCoupon(COUPON_CODE, USER_NOT_FOUND_USERNAME))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(USER_NOT_FOUND_MESSAGE);
    }

    @Test
    void useCoupon_shouldThrowCouponAlreadyUsedException() {
        user.getUsedCoupons().add(coupon);

        when(couponRepository.findByCode(COUPON_CODE)).thenReturn(coupon);
        when(userService.findByUsername(USER_USERNAME)).thenReturn(user);
        when(exceptionMessageAccessor.getMessage(any(), eq("coupon_already_used"))).thenReturn(COUPON_ALREADY_USED_MESSAGE);

        assertThatThrownBy(() -> couponService.useCoupon(COUPON_CODE, USER_USERNAME))
                .isInstanceOf(CouponAlreadyUsedException.class)
                .hasMessageContaining(COUPON_ALREADY_USED_MESSAGE);
    }

    @Test
    void useCoupon_shouldThrowCouponMaxUsagesExceededException() {
        coupon.setCurrentUsages(5);

        when(couponRepository.findByCode(COUPON_CODE)).thenReturn(coupon);
        when(exceptionMessageAccessor.getMessage(any(), eq("coupon_max_usages_exceeded"))).thenReturn(COUPON_MAX_USAGES_EXCEEDED_MESSAGE);

        assertThatThrownBy(() -> couponService.useCoupon(COUPON_CODE, USER_USERNAME))
                .isInstanceOf(CouponMaxUsagesExceededException.class)
                .hasMessageContaining(COUPON_MAX_USAGES_EXCEEDED_MESSAGE);
    }

    @Test
    void useCoupon_shouldThrowCouponNotValidForUserCountryException() {
        coupon.setDestinedCountry("Germany");

        when(couponRepository.findByCode(COUPON_CODE)).thenReturn(coupon);
        when(userService.findByUsername(USER_USERNAME)).thenReturn(user);
        when(exceptionMessageAccessor.getMessage(any(), eq("coupon_not_valid_for_user_country"))).thenReturn(COUPON_NOT_VALID_FOR_USER_COUNTRY_MESSAGE);

        assertThatThrownBy(() -> couponService.useCoupon(COUPON_CODE, USER_USERNAME))
                .isInstanceOf(CouponNotValidForUserCountryException.class)
                .hasMessageContaining(COUPON_NOT_VALID_FOR_USER_COUNTRY_MESSAGE);
    }
} 