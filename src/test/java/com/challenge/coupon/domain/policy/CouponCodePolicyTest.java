package com.challenge.coupon.domain.policy;

import com.challenge.coupon.exception.CouponValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponCodePolicyTest {

    @Test
    @DisplayName("Should return code unchanged when already 6 alphanumeric characters")
    void shouldSanitizeValidAlphanumericCode() {
        String result = CouponCodePolicy.sanitizeAndValidate("ABC123");
        assertThat(result).isEqualTo("ABC123");
    }

    @ParameterizedTest
    @ValueSource(strings = {"ABC-123", "A-B-C-1-2-3", "AB#C!1@2$3%", " ABC-123 ", "A.B.C.1.2.3"})
    @DisplayName("Should strip special characters and spaces and return 6 alphanumeric characters")
    void shouldStripSpecialCharactersAndReturnCleanCode(String input) {
        String result = CouponCodePolicy.sanitizeAndValidate(input);
        assertThat(result).isEqualTo("ABC123");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t\n"})
    @DisplayName("Should throw CouponValidationException when code is blank")
    void shouldThrowExceptionWhenCodeIsBlank(String blankCode) {
        assertThatThrownBy(() -> CouponCodePolicy.sanitizeAndValidate(blankCode))
                .isInstanceOf(CouponValidationException.class)
                .hasMessageContaining("Coupon code is required and cannot be blank");
    }

    @Test
    @DisplayName("Should throw CouponValidationException when code is null")
    void shouldThrowExceptionWhenCodeIsNull() {
        assertThatThrownBy(() -> CouponCodePolicy.sanitizeAndValidate(null))
                .isInstanceOf(CouponValidationException.class)
                .hasMessageContaining("Coupon code is required and cannot be blank");
    }

    @ParameterizedTest
    @ValueSource(strings = {"ABC", "A-1", "12345", "AB-12!"})
    @DisplayName("Should throw CouponValidationException when sanitized code has less than 6 characters")
    void shouldThrowExceptionWhenSanitizedLengthLessThanSix(String input) {
        assertThatThrownBy(() -> CouponCodePolicy.sanitizeAndValidate(input))
                .isInstanceOf(CouponValidationException.class)
                .hasMessageContaining("must contain exactly 6 alphanumeric characters");
    }

    @ParameterizedTest
    @ValueSource(strings = {"ABC1234", "ABC-12345", "A1B2C3D4E5"})
    @DisplayName("Should throw CouponValidationException when sanitized code has more than 6 characters")
    void shouldThrowExceptionWhenSanitizedLengthMoreThanSix(String input) {
        assertThatThrownBy(() -> CouponCodePolicy.sanitizeAndValidate(input))
                .isInstanceOf(CouponValidationException.class)
                .hasMessageContaining("must contain exactly 6 alphanumeric characters");
    }
}
