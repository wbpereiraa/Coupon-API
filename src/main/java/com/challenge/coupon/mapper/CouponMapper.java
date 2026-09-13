package com.challenge.coupon.mapper;

import com.challenge.coupon.dto.request.CreateCouponRequest;
import com.challenge.coupon.dto.response.CouponResponse;
import com.challenge.coupon.model.Coupon;
import org.springframework.stereotype.Component;

@Component
public class CouponMapper {

    public Coupon toEntity(CreateCouponRequest request) {
        if (request == null) {
            return null;
        }
        return Coupon.create(
                request.getCode(),
                request.getDescription(),
                request.getDiscountValue(),
                request.getExpirationDate(),
                request.getPublished()
        );
    }

    public CouponResponse toResponse(Coupon coupon) {
        if (coupon == null) {
            return null;
        }
        return CouponResponse.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .description(coupon.getDescription())
                .discountValue(coupon.getDiscountValue())
                .expirationDate(coupon.getExpirationDate())
                .status(coupon.getStatus())
                .published(coupon.isPublished())
                .redeemed(coupon.isRedeemed())
                .build();
    }
}
