package com.challenge.coupon.entity;

import com.challenge.coupon.domain.model.CouponStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "coupons")
public class CouponEntity {

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

    public CouponEntity() {
    }

    public CouponEntity(UUID id, String code, String description, Double discountValue, Instant expirationDate,
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

    public static Builder builder() {
        return new Builder();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(Double discountValue) {
        this.discountValue = discountValue;
    }

    public Instant getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Instant expirationDate) {
        this.expirationDate = expirationDate;
    }

    public CouponStatus getStatus() {
        return status;
    }

    public void setStatus(CouponStatus status) {
        this.status = status;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public boolean isRedeemed() {
        return redeemed;
    }

    public void setRedeemed(boolean redeemed) {
        this.redeemed = redeemed;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CouponEntity that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public static class Builder {
        private UUID id;
        private String code;
        private String description;
        private Double discountValue;
        private Instant expirationDate;
        private CouponStatus status;
        private boolean published;
        private boolean redeemed;
        private Instant createdAt;
        private Instant updatedAt;
        private Instant deletedAt;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder discountValue(Double discountValue) {
            this.discountValue = discountValue;
            return this;
        }

        public Builder expirationDate(Instant expirationDate) {
            this.expirationDate = expirationDate;
            return this;
        }

        public Builder status(CouponStatus status) {
            this.status = status;
            return this;
        }

        public Builder published(boolean published) {
            this.published = published;
            return this;
        }

        public Builder redeemed(boolean redeemed) {
            this.redeemed = redeemed;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder deletedAt(Instant deletedAt) {
            this.deletedAt = deletedAt;
            return this;
        }

        public CouponEntity build() {
            return new CouponEntity(id, code, description, discountValue, expirationDate, status, published, redeemed, createdAt, updatedAt, deletedAt);
        }
    }
}