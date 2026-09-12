package com.challenge.coupon.domain.policy;

import com.challenge.coupon.exception.CouponValidationException;

import java.util.Locale;

public final class CouponDiscountPolicy {

    public static final double MINIMUM_DISCOUNT = 0.5;

    private CouponDiscountPolicy() {
    }

    public static void validate(Double discountValue) {
        if (discountValue == null) {
            throw new CouponValidationException("Coupon discount value is required.");
        }

        if (discountValue < MINIMUM_DISCOUNT) {
            throw new CouponValidationException(
                String.format(Locale.US, "Coupon discount value must be at least %.1f. Provided: %.2f",
                    MINIMUM_DISCOUNT, discountValue)
            );
        }
    }
}