package com.challenge.coupon.exception;

import java.util.UUID;

public class CouponAlreadyDeletedException extends BusinessException {
    public CouponAlreadyDeletedException(String message) {
        super(message);
    }

    public CouponAlreadyDeletedException(UUID id) {
        super(String.format("Coupon with id '%s' has already been deleted.", id));
    }
}
