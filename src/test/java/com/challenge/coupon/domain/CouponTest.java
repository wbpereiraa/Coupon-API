package com.challenge.coupon.domain;

import com.challenge.coupon.domain.model.Coupon;
import com.challenge.coupon.domain.model.CouponStatus;
import com.challenge.coupon.exception.CouponAlreadyDeletedException;
import com.challenge.coupon.exception.CouponValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
    @DisplayName("Should correctly reconstitute coupon from persistence")
    void shouldReconstituteCoupon() {
        UUID id = UUID.randomUUID();
        Coupon coupon = Coupon.reconstitute(
                id,
                "XYZ789",
                "Cupom Reconstituido",
                15.5,
                futureExpiration,
                CouponStatus.ACTIVE,
                true,
                false,
                fixedNow,
                fixedNow,
                null
        );

        assertThat(coupon.getId()).isEqualTo(id);
        assertThat(coupon.getCode()).isEqualTo("XYZ789");
        assertThat(coupon.getDescription()).isEqualTo("Cupom Reconstituido");
        assertThat(coupon.getDiscountValue()).isEqualTo(15.5);
        assertThat(coupon.getStatus()).isEqualTo(CouponStatus.ACTIVE);
        assertThat(coupon.isPublished()).isTrue();
    }
}
