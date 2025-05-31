package com.jgalat.coupon.springboot.service.coupon.impl;

import com.jgalat.coupon.springboot.service.coupon.CouponService;
import com.jgalat.coupon.springboot.service.user.UserService;

import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;

import com.jgalat.coupon.springboot.repository.CouponRepository;
import com.jgalat.coupon.springboot.exceptions.CouponAlreadyUsedException;
import com.jgalat.coupon.springboot.exceptions.CouponMaxUsagesExceededException;
import com.jgalat.coupon.springboot.exceptions.CouponMaxUsagesRetriesExceededException;
import com.jgalat.coupon.springboot.exceptions.CouponNotFoundException;
import com.jgalat.coupon.springboot.exceptions.CouponNotValidForUserCountryException;
import com.jgalat.coupon.springboot.exceptions.UserNotFoundException;
import com.jgalat.coupon.springboot.model.Coupon;
import com.jgalat.coupon.springboot.model.User;
import com.jgalat.coupon.springboot.model.payload.AddCouponRequest;
import com.jgalat.coupon.springboot.model.payload.AddCouponResponse;
import com.jgalat.coupon.springboot.model.payload.UseCouponResponse;
import com.jgalat.coupon.springboot.utils.GeneralMessageAccessor;
import com.jgalat.coupon.springboot.utils.ExceptionMessageAccessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import org.hibernate.StaleObjectStateException;
import org.springframework.retry.annotation.Backoff;        
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Recover;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private static final int _INITIAL_CURRENT_USAGES = 0;
    private final CouponRepository couponRepository;
    private final UserService userService;
    private final GeneralMessageAccessor generalMessageAccessor;
    private final ExceptionMessageAccessor exceptionMessageAccessor;
    @Override
    @Transactional(rollbackOn = Exception.class)
    public AddCouponResponse saveCoupon(AddCouponRequest coupon) {
        Coupon newCoupon = Coupon.builder()
            .code(coupon.getCouponCode().toLowerCase())
            .maxUsages(coupon.getMaxUsages())
            .destinedCountry(coupon.getDestinedCountry())
            .currentUsages(_INITIAL_CURRENT_USAGES)
            .build();

        log.info("Saving newCoupon: {}", newCoupon);
        couponRepository.save(newCoupon);
        return new AddCouponResponse(generalMessageAccessor.getMessage(null, "coupon_added_successfully", newCoupon));
    }

    @Override
    @Retryable(retryFor = {OptimisticLockException.class, StaleObjectStateException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @Transactional(rollbackOn = Exception.class)
    public UseCouponResponse useCoupon(String couponCode, String userName) throws CouponMaxUsagesExceededException, CouponAlreadyUsedException, 
      CouponNotValidForUserCountryException, UserNotFoundException, CouponNotFoundException {
        Coupon coupon = couponRepository.findByCode(couponCode);
        checkIfCouponWasFound(couponCode, coupon);
        checkIfCouponMaxUsagesExceeded(coupon);

        User user = userService.findByUsername(userName);
        checkIfUserWasFound(userName, user);
        checkIfCouponAlreadyUsed(coupon, user);
        checkIfCouponNotValidForUserCountry(coupon, user);

        user.getUsedCoupons().add(coupon);
        log.info("Adding coupon to user: {}", user);
        userService.saveUser(user);

        coupon.setCurrentUsages(coupon.getCurrentUsages() + 1);
        coupon.getUsersUsingTheCoupon().add(user);
        log.info("Incrementing coupon usages: {}", coupon);
        couponRepository.save(coupon);

        log.info("Coupon used successfully: {}", coupon);
        return new UseCouponResponse(generalMessageAccessor.getMessage(null, "coupon_used_successfully", coupon));
    }

    private void checkIfCouponMaxUsagesExceeded(Coupon coupon) throws CouponMaxUsagesExceededException {
        if (coupon.getCurrentUsages() + 1 > coupon.getMaxUsages()) {
            log.error("Coupon max usages exceeded: {}", coupon);
            throw new CouponMaxUsagesExceededException(exceptionMessageAccessor.getMessage(null, "coupon_max_usages_exceeded"));
        }
    }

    private void checkIfCouponWasFound(String couponCode, Coupon coupon) throws CouponNotFoundException {
        if (coupon == null) {
            log.error("Coupon not found: {}", couponCode);
            throw new CouponNotFoundException(exceptionMessageAccessor.getMessage(null, "coupon_not_found", couponCode));
        }
    }

    private void checkIfUserWasFound(String userName, User user) throws UserNotFoundException {
        if (user == null) {
            log.error("User not found: {}", userName);
            throw new UserNotFoundException(exceptionMessageAccessor.getMessage(null, "user_not_found", userName));  
        }
    }

    private void checkIfCouponAlreadyUsed(Coupon coupon, User user) throws CouponAlreadyUsedException {
        if (user.getUsedCoupons().contains(coupon)) {
            log.error("Coupon already used: {}, user used coupons: {}", coupon, user.getUsedCoupons());
            throw new CouponAlreadyUsedException(exceptionMessageAccessor.getMessage(null, "coupon_already_used"));
        }
    }   

    private void checkIfCouponNotValidForUserCountry(Coupon coupon, User user) throws CouponNotValidForUserCountryException {
        if (!coupon.getDestinedCountry().equals(user.getCountry())) {
            log.error("Coupon not valid for user country: {}, coupon: {}", user.getCountry(), coupon);
            throw new CouponNotValidForUserCountryException(exceptionMessageAccessor.getMessage(null, "coupon_not_valid_for_user_country"));
        }
    }

    @Recover
    private void recoverOptimisticLockException(OptimisticLockException exception) throws CouponMaxUsagesRetriesExceededException{
        log.error("Optimistic lock exception: {}", exception);
        throw new CouponMaxUsagesRetriesExceededException(exceptionMessageAccessor.getMessage(null, "coupon_max_usages_retries_exceeded"));
    }

    @Recover
    private void recoverStaleObjectStateException(StaleObjectStateException exception) throws CouponMaxUsagesRetriesExceededException{
        log.error("Stale object state exception: {}", exception);
        throw new CouponMaxUsagesRetriesExceededException(exceptionMessageAccessor.getMessage(null, "coupon_max_usages_retries_exceeded"));
    }

    @Recover
    private void recoverNotValidForUserCountryException(CouponNotValidForUserCountryException exception) throws CouponNotValidForUserCountryException{
        throw exception;
    }

    @Recover
    private void recoverCouponNotFoundException(CouponNotFoundException exception) throws CouponNotFoundException{
        throw exception;
    }

    @Recover
    private void recoverUserNotFoundException(UserNotFoundException exception) throws UserNotFoundException{
        throw exception;
    }   

    @Recover
    private void recoverCouponMaxUsagesExceededException(CouponMaxUsagesExceededException exception) throws CouponMaxUsagesExceededException{
        throw exception;
    }

    @Recover
    private void recoverCouponAlreadyUsedException(CouponAlreadyUsedException exception) throws CouponAlreadyUsedException{
        throw exception;
    }

}
