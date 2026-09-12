package com.challenge.coupon.domain.model;

import com.challenge.coupon.domain.policy.CouponCodePolicy;
import com.challenge.coupon.domain.policy.CouponDiscountPolicy;
import com.challenge.coupon.domain.policy.CouponExpirationPolicy;
import com.challenge.coupon.exception.CouponAlreadyDeletedException;
import com.challenge.coupon.exception.CouponValidationException;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Coupon {

    private final UUID id;
    private final String code;
    private final String description;
    private final Double discountValue;
    private final Instant expirationDate;
    private CouponStatus status;
    private boolean published;
    private boolean redeemed;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    private Coupon(UUID id,
                   String code,
                   String description,
                   Double discountValue,
                   Instant expirationDate,
                   CouponStatus status,
                   boolean published,
                   boolean redeemed,
                   Instant createdAt,
                   Instant updatedAt,
                   Instant deletedAt) {
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
                                Boolean published,
                                Instant now) {
        String sanitizedCode = CouponCodePolicy.sanitizeAndValidate(rawCode);

        if (description == null || description.isBlank()) {
            throw new CouponValidationException("Coupon description is required and cannot be blank.");
        }

        CouponDiscountPolicy.validate(discountValue);
        CouponExpirationPolicy.validate(expirationDate, now);

        Instant creationTime = (now != null) ? now : Instant.now();

        return new Coupon(
                UUID.randomUUID(),
                sanitizedCode,
                description.trim(),
                discountValue,
                expirationDate,
                CouponStatus.ACTIVE,
                Boolean.TRUE.equals(published),
                false,
                creationTime,
                creationTime,
                null
        );
    }

    public static Coupon reconstitute(UUID id,
                                      String code,
                                      String description,
                                      Double discountValue,
                                      Instant expirationDate,
                                      CouponStatus status,
                                      boolean published,
                                      boolean redeemed,
                                      Instant createdAt,
                                      Instant updatedAt,
                                      Instant deletedAt) {
        return new Coupon(
                id,
                code,
                description,
                discountValue,
                expirationDate,
                status,
                published,
                redeemed,
                createdAt,
                updatedAt,
                deletedAt
        );
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

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public Double getDiscountValue() {
        return discountValue;
    }

    public Instant getExpirationDate() {
        return expirationDate;
    }

    public CouponStatus getStatus() {
        return status;
    }

    public boolean isPublished() {
        return published;
    }

    public boolean isRedeemed() {
        return redeemed;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
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
