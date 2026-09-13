package com.challenge.coupon.service;

import com.challenge.coupon.dto.request.CreateCouponRequest;
import com.challenge.coupon.dto.response.CouponResponse;

import java.util.UUID;

public interface CouponService {

    CouponResponse create(CreateCouponRequest request);

    CouponResponse getById(UUID id);

    void delete(UUID id);
}

