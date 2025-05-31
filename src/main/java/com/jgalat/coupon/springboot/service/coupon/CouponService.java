package com.jgalat.coupon.springboot.service.coupon;
import com.jgalat.coupon.springboot.exceptions.CouponAlreadyUsedException;
import com.jgalat.coupon.springboot.exceptions.CouponMaxUsagesExceededException;
import com.jgalat.coupon.springboot.exceptions.CouponMaxUsagesRetriesExceededException;
import com.jgalat.coupon.springboot.exceptions.CouponNotFoundException;
import com.jgalat.coupon.springboot.exceptions.CouponNotValidForUserCountryException;
import com.jgalat.coupon.springboot.exceptions.UserNotFoundException;
import com.jgalat.coupon.springboot.model.payload.AddCouponRequest;
import com.jgalat.coupon.springboot.model.payload.AddCouponResponse;
import com.jgalat.coupon.springboot.model.payload.UseCouponResponse;

public interface CouponService {

    AddCouponResponse saveCoupon(AddCouponRequest coupon);

    UseCouponResponse useCoupon(String couponCode, String userName) throws CouponMaxUsagesExceededException, CouponAlreadyUsedException, 
      CouponNotValidForUserCountryException, UserNotFoundException, CouponNotFoundException, CouponMaxUsagesRetriesExceededException;

}
