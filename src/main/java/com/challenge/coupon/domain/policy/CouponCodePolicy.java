package com.challenge.coupon.domain.policy;

import com.challenge.coupon.exception.CouponValidationException;

public final class CouponCodePolicy {

    public static final int REQUIRED_LENGTH = 6;
    private static final String NON_ALPHANUMERIC_REGEX = "[^a-zA-Z0-9]";

    private CouponCodePolicy() {
    }

    public static String sanitizeAndValidate(String code) {
        if (code == null || code.isBlank()) {
            throw new CouponValidationException("Coupon code is required and cannot be blank.");
        }

        String sanitized = code.replaceAll(NON_ALPHANUMERIC_REGEX, "");

        if (sanitized.length() != REQUIRED_LENGTH) {
            throw new CouponValidationException(
                String.format("Coupon code must contain exactly %d alphanumeric characters after removing special characters. Sanitized result was '%s' (length: %d).",
                    REQUIRED_LENGTH, sanitized, sanitized.length())
            );
        }

        return sanitized;
    }
}
