package com.challenge.coupon.domain.policy;

import com.challenge.coupon.exception.CouponValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponDiscountPolicyTest {

    @Test
    @DisplayName("Should accept discount value of exactly 0.5")
    void shouldAcceptMinimumDiscount() {
        assertThatCode(() -> CouponDiscountPolicy.validate(0.5))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.51, 1.0, 10.5, 100.0, 9999.99})
    @DisplayName("Should accept discount value greater than 0.5")
    void shouldAcceptDiscountGreaterThanMinimum(double value) {
        assertThatCode(() -> CouponDiscountPolicy.validate(value))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.49, 0.1, 0.0, -0.5, -10.0})
    @DisplayName("Should throw CouponValidationException when discount value is less than 0.5")
    void shouldRejectDiscountLessThanMinimum(double value) {
        assertThatThrownBy(() -> CouponDiscountPolicy.validate(value))
                .isInstanceOf(CouponValidationException.class)
                .hasMessageContaining("must be at least 0.5");
    }

    @Test
    @DisplayName("Should throw CouponValidationException when discount value is null")
    void shouldRejectNullDiscount() {
        assertThatThrownBy(() -> CouponDiscountPolicy.validate(null))
                .isInstanceOf(CouponValidationException.class)
                .hasMessageContaining("Coupon discount value is required");
    }
}
