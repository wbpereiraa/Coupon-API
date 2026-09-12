package com.challenge.coupon.service.impl;

import com.challenge.coupon.domain.model.Coupon;
import com.challenge.coupon.dto.request.CreateCouponRequest;
import com.challenge.coupon.dto.response.CouponResponse;
import com.challenge.coupon.entity.CouponEntity;
import com.challenge.coupon.exception.ResourceNotFoundException;
import com.challenge.coupon.mapper.CouponMapper;
import com.challenge.coupon.repository.CouponRepository;
import com.challenge.coupon.service.CouponService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
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

        Coupon domainCoupon = Coupon.create(
                request.getCode(),
                request.getDescription(),
                request.getDiscountValue(),
                request.getExpirationDate(),
                request.getPublished(),
                Instant.now()
        );

        CouponEntity entity = couponMapper.toEntity(domainCoupon);
        CouponEntity savedEntity = couponRepository.save(entity);

        log.info("Coupon created successfully with id '{}' and sanitized code '{}'",
                savedEntity.getId(), savedEntity.getCode());

        return couponMapper.toResponse(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public CouponResponse getById(UUID id) {
        log.info("Fetching coupon with id '{}'", id);

        CouponEntity entity = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        return couponMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.info("Soft-deleting coupon with id '{}'", id);

        CouponEntity entity = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        Coupon domainCoupon = couponMapper.toDomain(entity);
        domainCoupon.delete(Instant.now());

        CouponEntity updatedEntity = couponMapper.toEntity(domainCoupon);
        couponRepository.save(updatedEntity);

        log.info("Coupon with id '{}' successfully soft-deleted", id);
    }
}