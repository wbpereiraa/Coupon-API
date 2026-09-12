package com.challenge.coupon.domain.policy;

import com.challenge.coupon.exception.CouponValidationException;

import java.time.Instant;

public final class CouponExpirationPolicy {

    private CouponExpirationPolicy() {
    }

    public static void validate(Instant expirationDate, Instant now) {
        if (expirationDate == null) {
            throw new CouponValidationException("Coupon expiration date is required.");
        }

        if (expirationDate.isBefore(now)) {
            throw new CouponValidationException(
                String.format("Coupon expiration date cannot be in the past. Expiration: %s, Current time: %s",
                    expirationDate, now)
            );
        }
    }
}
