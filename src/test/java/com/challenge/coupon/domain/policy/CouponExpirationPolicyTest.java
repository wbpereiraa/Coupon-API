package com.challenge.coupon.domain.policy;

import com.challenge.coupon.exception.CouponValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponExpirationPolicyTest {

    private final Instant now = Instant.parse("2026-09-12T12:00:00Z");

    @Test
    @DisplayName("Should accept future expiration date")
    void shouldAcceptFutureExpiration() {
        Instant future = now.plus(1, ChronoUnit.DAYS);
        assertThatCode(() -> CouponExpirationPolicy.validate(future, now))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should reject past expiration date")
    void shouldRejectPastExpiration() {
        Instant past = now.minus(1, ChronoUnit.SECONDS);
        assertThatThrownBy(() -> CouponExpirationPolicy.validate(past, now))
                .isInstanceOf(CouponValidationException.class)
                .hasMessageContaining("cannot be in the past");
    }

    @Test
    @DisplayName("Should reject null expiration date")
    void shouldRejectNullExpiration() {
        assertThatThrownBy(() -> CouponExpirationPolicy.validate(null, now))
                .isInstanceOf(CouponValidationException.class)
                .hasMessageContaining("Coupon expiration date is required");
    }
}
