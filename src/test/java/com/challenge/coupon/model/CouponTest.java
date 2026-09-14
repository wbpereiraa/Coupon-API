package com.challenge.coupon.model;

import com.challenge.coupon.exception.CouponAlreadyDeletedException;
import com.challenge.coupon.exception.CouponValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponTest {

    private final Instant fixedNow = Instant.parse("2026-09-12T10:00:00Z");
    private final Instant futureExpiration = fixedNow.plus(30, ChronoUnit.DAYS);

    @Test
    @DisplayName("Should successfully create a coupon with valid data and sanitize code")
    void shouldCreateValidCoupon() {
        Coupon coupon = Coupon.create(
                "ABC-123",
                "Desconto de boas-vindas",
                0.8,
                futureExpiration,
                false,
                fixedNow
        );

        assertThat(coupon.getId()).isNotNull();
        assertThat(coupon.getCode()).isEqualTo("ABC123");
        assertThat(coupon.getDescription()).isEqualTo("Desconto de boas-vindas");
        assertThat(coupon.getDiscountValue()).isEqualTo(0.8);
        assertThat(coupon.getExpirationDate()).isEqualTo(futureExpiration);
        assertThat(coupon.getStatus()).isEqualTo(CouponStatus.ACTIVE);
        assertThat(coupon.isPublished()).isFalse();
        assertThat(coupon.isRedeemed()).isFalse();
        assertThat(coupon.getCreatedAt()).isEqualTo(fixedNow);
        assertThat(coupon.getUpdatedAt()).isEqualTo(fixedNow);
        assertThat(coupon.getDeletedAt()).isNull();
        assertThat(coupon.isDeleted()).isFalse();
    }

    @Test
    @DisplayName("Should create coupon as published when published parameter is true")
    void shouldCreateCouponAsPublished() {
        Coupon coupon = Coupon.create(
                "XYZ-999",
                "Cupom publicado",
                10.0,
                futureExpiration,
                true,
                fixedNow
        );

        assertThat(coupon.isPublished()).isTrue();
    }

    @Test
    @DisplayName("Should reject coupon creation when description is blank or null")
    void shouldRejectBlankDescription() {
        assertThatThrownBy(() -> Coupon.create("ABC-123", "   ", 1.0, futureExpiration, false, fixedNow))
                .isInstanceOf(CouponValidationException.class)
                .hasMessageContaining("Coupon description is required and cannot be blank");

        assertThatThrownBy(() -> Coupon.create("ABC-123", null, 1.0, futureExpiration, false, fixedNow))
                .isInstanceOf(CouponValidationException.class)
                .hasMessageContaining("Coupon description is required and cannot be blank");
    }

    @ParameterizedTest
    @ValueSource(strings = {"ABC-123", "A-B-C-1-2-3", "AB#C!1@2$3%", " ABC-123 ", "A.B.C.1.2.3"})
    @DisplayName("Should strip special characters and spaces and return 6 alphanumeric characters")
    void shouldStripSpecialCharactersAndReturnCleanCode(String input) {
        String result = Coupon.sanitizeAndValidateCode(input);
        assertThat(result).isEqualTo("ABC123");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t\n"})
    @DisplayName("Should throw CouponValidationException when code is blank")
    void shouldThrowExceptionWhenCodeIsBlank(String blankCode) {
        assertThatThrownBy(() -> Coupon.sanitizeAndValidateCode(blankCode))
                .isInstanceOf(CouponValidationException.class)
                .hasMessageContaining("Coupon code is required and cannot be blank");
    }

    @Test
    @DisplayName("Should throw CouponValidationException when code is null")
    void shouldThrowExceptionWhenCodeIsNull() {
        assertThatThrownBy(() -> Coupon.sanitizeAndValidateCode(null))
                .isInstanceOf(CouponValidationException.class)
                .hasMessageContaining("Coupon code is required and cannot be blank");
    }

    @ParameterizedTest
    @ValueSource(strings = {"ABC", "A-1", "12345", "AB-12!"})
    @DisplayName("Should throw CouponValidationException when sanitized code has less than 6 characters")
    void shouldThrowExceptionWhenSanitizedLengthLessThanSix(String input) {
        assertThatThrownBy(() -> Coupon.sanitizeAndValidateCode(input))
                .isInstanceOf(CouponValidationException.class)
                .hasMessageContaining("must contain exactly 6 alphanumeric characters");
    }

    @ParameterizedTest
    @ValueSource(strings = {"ABC1234", "ABC-12345", "A1B2C3D4E5"})
    @DisplayName("Should throw CouponValidationException when sanitized code has more than 6 characters")
    void shouldThrowExceptionWhenSanitizedLengthMoreThanSix(String input) {
        assertThatThrownBy(() -> Coupon.sanitizeAndValidateCode(input))
                .isInstanceOf(CouponValidationException.class)
                .hasMessageContaining("must contain exactly 6 alphanumeric characters");
    }

    @Test
    @DisplayName("Should validate minimum discount value (>= 0.5)")
    void shouldValidateMinimumDiscount() {
        Coupon.validateDiscount(0.5);
        Coupon.validateDiscount(10.0);

        assertThatThrownBy(() -> Coupon.validateDiscount(null))
                .isInstanceOf(CouponValidationException.class)
                .hasMessageContaining("Coupon discount value is required");

        assertThatThrownBy(() -> Coupon.validateDiscount(0.49))
                .isInstanceOf(CouponValidationException.class)
                .hasMessageContaining("must be at least 0.5");
    }

    @Test
    @DisplayName("Should validate expiration date is not null and not in past")
    void shouldValidateExpirationDate() {
        Coupon.validateExpiration(futureExpiration, fixedNow);

        assertThatThrownBy(() -> Coupon.validateExpiration(null, fixedNow))
                .isInstanceOf(CouponValidationException.class)
                .hasMessageContaining("Coupon expiration date is required");

        assertThatThrownBy(() -> Coupon.validateExpiration(fixedNow.minus(1, ChronoUnit.DAYS), fixedNow))
                .isInstanceOf(CouponValidationException.class)
                .hasMessageContaining("cannot be in the past");
    }

    @Test
    @DisplayName("Should perform soft delete and update timestamps")
    void shouldSoftDeleteCoupon() {
        Coupon coupon = Coupon.create(
                "ABC-123",
                "Cupom a deletar",
                5.0,
                futureExpiration,
                false,
                fixedNow
        );

        Instant deleteTime = fixedNow.plus(1, ChronoUnit.HOURS);
        coupon.delete(deleteTime);

        assertThat(coupon.getStatus()).isEqualTo(CouponStatus.DELETED);
        assertThat(coupon.isDeleted()).isTrue();
        assertThat(coupon.getDeletedAt()).isEqualTo(deleteTime);
        assertThat(coupon.getUpdatedAt()).isEqualTo(deleteTime);
    }

    @Test
    @DisplayName("Should throw CouponAlreadyDeletedException when trying to delete already deleted coupon")
    void shouldThrowExceptionWhenDeletingAlreadyDeletedCoupon() {
        Coupon coupon = Coupon.create(
                "ABC-123",
                "Cupom",
                5.0,
                futureExpiration,
                false,
                fixedNow
        );

        coupon.delete(fixedNow.plus(1, ChronoUnit.HOURS));

        assertThatThrownBy(() -> coupon.delete(fixedNow.plus(2, ChronoUnit.HOURS)))
                .isInstanceOf(CouponAlreadyDeletedException.class)
                .hasMessageContaining(coupon.getId().toString())
                .hasMessageContaining("has already been deleted");
    }

    @Test
    @DisplayName("Should expose the no-arg constructor and mutable entity properties")
    void shouldSupportEntityConstructionAndPropertyUpdates() {
        Coupon coupon = new Coupon();
        UUID id = UUID.randomUUID();
        Instant expiration = fixedNow.plus(60, ChronoUnit.DAYS);
        Instant created = fixedNow.minus(1, ChronoUnit.DAYS);
        Instant updated = fixedNow.plus(1, ChronoUnit.HOURS);
        Instant deleted = fixedNow.plus(2, ChronoUnit.HOURS);

        coupon.setId(id);
        coupon.setCode("ABC123");
        coupon.setDescription("Atualizado");
        coupon.setDiscountValue(2.0);
        coupon.setExpirationDate(expiration);
        coupon.setStatus(CouponStatus.ACTIVE);
        coupon.setPublished(true);
        coupon.setRedeemed(true);
        coupon.setCreatedAt(created);
        coupon.setUpdatedAt(updated);
        coupon.setDeletedAt(deleted);

        assertThat(coupon.getId()).isEqualTo(id);
        assertThat(coupon.getCode()).isEqualTo("ABC123");
        assertThat(coupon.getDescription()).isEqualTo("Atualizado");
        assertThat(coupon.getDiscountValue()).isEqualTo(2.0);
        assertThat(coupon.getExpirationDate()).isEqualTo(expiration);
        assertThat(coupon.getStatus()).isEqualTo(CouponStatus.ACTIVE);
        assertThat(coupon.isPublished()).isTrue();
        assertThat(coupon.isRedeemed()).isTrue();
        assertThat(coupon.getCreatedAt()).isEqualTo(created);
        assertThat(coupon.getUpdatedAt()).isEqualTo(updated);
        assertThat(coupon.getDeletedAt()).isEqualTo(deleted);
        assertThat(coupon.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("Should create and delete a coupon using default current time")
    void shouldUseCurrentTimeWhenTimeIsNotProvided() {
        Coupon coupon = Coupon.create(
                "ABC123",
                "Cupom com horário padrão",
                1.0,
                Instant.now().plus(1, ChronoUnit.DAYS),
                null
        );

        assertThat(coupon.getCreatedAt()).isNotNull();
        assertThat(coupon.getUpdatedAt()).isEqualTo(coupon.getCreatedAt());
        assertThat(coupon.isPublished()).isFalse();

        coupon.delete();

        assertThat(coupon.getDeletedAt()).isNotNull();
        assertThat(coupon.getUpdatedAt()).isEqualTo(coupon.getDeletedAt());
    }
}
