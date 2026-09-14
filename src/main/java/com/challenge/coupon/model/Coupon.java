package com.challenge.coupon.model;

import com.challenge.coupon.exception.CouponAlreadyDeletedException;
import com.challenge.coupon.exception.CouponValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "coupons")
@Getter
@Setter
public class Coupon {

    public static final int REQUIRED_CODE_LENGTH = 6;
    public static final double MINIMUM_DISCOUNT = 0.5;
    private static final String NON_ALPHANUMERIC_REGEX = "[^a-zA-Z0-9]";

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "code", length = 6, nullable = false)
    private String code;

    @Column(name = "description", length = 1000, nullable = false)
    private String description;

    @Column(name = "discount_value", nullable = false, columnDefinition = "NUMERIC(10, 2)")
    private Double discountValue;

    @Column(name = "expiration_date", nullable = false)
    private Instant expirationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private CouponStatus status;

    @Column(name = "published", nullable = false)
    private boolean published;

    @Column(name = "redeemed", nullable = false)
    private boolean redeemed;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public Coupon() {
    }

    public Coupon(UUID id, String code, String description, Double discountValue, Instant expirationDate,
                  CouponStatus status, boolean published, boolean redeemed, Instant createdAt,
                  Instant updatedAt, Instant deletedAt) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.discountValue = discountValue;
        this.expirationDate = expirationDate;
        this.status = status;
        this.published = published;
        this.redeemed = redeemed;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public static Coupon create(String rawCode,
                                String description,
                                Double discountValue,
                                Instant expirationDate,
                                Boolean published) {
        return create(rawCode, description, discountValue, expirationDate, published, Instant.now());
    }

    public static Coupon create(String rawCode,
                                String description,
                                Double discountValue,
                                Instant expirationDate,
                                Boolean published,
                                Instant now) {
        Instant effectiveNow = (now != null) ? now : Instant.now();
        String sanitizedCode = sanitizeAndValidateCode(rawCode);

        if (description == null || description.isBlank()) {
            throw new CouponValidationException("Coupon description is required and cannot be blank.");
        }

        validateDiscount(discountValue);
        validateExpiration(expirationDate, effectiveNow);

        return new Coupon(
                UUID.randomUUID(),
                sanitizedCode,
                description.trim(),
                discountValue,
                expirationDate,
                CouponStatus.ACTIVE,
                Boolean.TRUE.equals(published),
                false,
                effectiveNow,
                effectiveNow,
                null
        );
    }

    public static String sanitizeAndValidateCode(String rawCode) {
        if (rawCode == null || rawCode.isBlank()) {
            throw new CouponValidationException("Coupon code is required and cannot be blank.");
        }

        String sanitized = rawCode.replaceAll(NON_ALPHANUMERIC_REGEX, "");

        if (sanitized.length() != REQUIRED_CODE_LENGTH) {
            throw new CouponValidationException(
                    String.format("Coupon code must contain exactly %d alphanumeric characters after removing special characters. Sanitized result was '%s' (length: %d).",
                            REQUIRED_CODE_LENGTH, sanitized, sanitized.length())
            );
        }

        return sanitized;
    }

    public static void validateDiscount(Double discountValue) {
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

    public static void validateExpiration(Instant expirationDate, Instant now) {
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

    public void delete() {
        delete(Instant.now());
    }

    public void delete(Instant now) {
        if (isDeleted()) {
            throw new CouponAlreadyDeletedException(this.id);
        }
        Instant deletionTime = (now != null) ? now : Instant.now();
        this.status = CouponStatus.DELETED;
        this.deletedAt = deletionTime;
        this.updatedAt = deletionTime;
    }

    public boolean isDeleted() {
        return this.status == CouponStatus.DELETED || this.deletedAt != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coupon coupon)) return false;
        return Objects.equals(id, coupon.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
