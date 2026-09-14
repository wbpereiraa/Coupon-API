package com.challenge.coupon.service;

import com.challenge.coupon.dto.request.CreateCouponRequest;
import com.challenge.coupon.dto.response.CouponResponse;
import com.challenge.coupon.exception.ResourceNotFoundException;
import com.challenge.coupon.mapper.CouponMapper;
import com.challenge.coupon.model.Coupon;
import com.challenge.coupon.repository.CouponRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CouponServiceImpl implements CouponService {

    private static final Logger log = LoggerFactory.getLogger(CouponServiceImpl.class);

    private final CouponRepository couponRepository;
    private final CouponMapper couponMapper;

    public CouponServiceImpl(CouponRepository couponRepository, CouponMapper couponMapper) {
        this.couponRepository = couponRepository;
        this.couponMapper = couponMapper;
    }

    @Override
    @Transactional
    public CouponResponse create(CreateCouponRequest request) {
        log.info("Creating new coupon with raw code '{}'", request.getCode());

        Coupon coupon = Coupon.create(
                request.getCode(),
                request.getDescription(),
                request.getDiscountValue(),
                request.getExpirationDate(),
                request.getPublished()
        );

        Coupon saved = couponRepository.save(coupon);

        log.info("Coupon created successfully with id '{}' and sanitized code '{}'",
                saved.getId(), saved.getCode());

        return couponMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CouponResponse getById(UUID id) {
        log.info("Fetching coupon with id '{}'", id);

        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        return couponMapper.toResponse(coupon);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.info("Soft-deleting coupon with id '{}'", id);

        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        coupon.delete();
        couponRepository.save(coupon);

        log.info("Coupon with id '{}' successfully soft-deleted", id);
    }
}

