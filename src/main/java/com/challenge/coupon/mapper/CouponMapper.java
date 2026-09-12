package com.challenge.coupon.mapper;

import com.challenge.coupon.domain.model.Coupon;
import com.challenge.coupon.dto.response.CouponResponse;
import com.challenge.coupon.entity.CouponEntity;
import org.springframework.stereotype.Component;

@Component
public class CouponMapper {

    public CouponEntity toEntity(Coupon domain) {
        if (domain == null) {
            return null;
        }
        return CouponEntity.builder()
                .id(domain.getId())
                .code(domain.getCode())
                .description(domain.getDescription())
                .discountValue(domain.getDiscountValue())
                .expirationDate(domain.getExpirationDate())
                .status(domain.getStatus())
                .published(domain.isPublished())
                .redeemed(domain.isRedeemed())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .deletedAt(domain.getDeletedAt())
                .build();
    }

    public Coupon toDomain(CouponEntity entity) {
        if (entity == null) {
            return null;
        }
        return Coupon.reconstitute(
                entity.getId(),
                entity.getCode(),
                entity.getDescription(),
                entity.getDiscountValue(),
                entity.getExpirationDate(),
                entity.getStatus(),
                entity.isPublished(),
                entity.isRedeemed(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }

    public CouponResponse toResponse(Coupon domain) {
        if (domain == null) {
            return null;
        }
        return CouponResponse.builder()
                .id(domain.getId())
                .code(domain.getCode())
                .description(domain.getDescription())
                .discountValue(domain.getDiscountValue())
                .expirationDate(domain.getExpirationDate())
                .status(domain.getStatus())
                .published(domain.isPublished())
                .redeemed(domain.isRedeemed())
                .build();
    }

    public CouponResponse toResponse(CouponEntity entity) {
        if (entity == null) {
            return null;
        }
        return CouponResponse.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .description(entity.getDescription())
                .discountValue(entity.getDiscountValue())
                .expirationDate(entity.getExpirationDate())
                .status(entity.getStatus())
                .published(entity.isPublished())
                .redeemed(entity.isRedeemed())
                .build();
    }
}
