package com.challenge.coupon.service;

import com.challenge.coupon.domain.model.CouponStatus;
import com.challenge.coupon.dto.request.CreateCouponRequest;
import com.challenge.coupon.dto.response.CouponResponse;
import com.challenge.coupon.entity.CouponEntity;
import com.challenge.coupon.exception.CouponAlreadyDeletedException;
import com.challenge.coupon.exception.ResourceNotFoundException;
import com.challenge.coupon.mapper.CouponMapper;
import com.challenge.coupon.repository.CouponRepository;
import com.challenge.coupon.service.impl.CouponServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CouponServiceImplTest {

    @Mock
    private CouponRepository couponRepository;

    private CouponMapper couponMapper;
    private CouponServiceImpl couponService;

    private final Instant futureDate = Instant.now().plus(10, ChronoUnit.DAYS);

    @BeforeEach
    void setUp() {
        couponMapper = new CouponMapper();
        couponService = new CouponServiceImpl(couponRepository, couponMapper);
    }

    @Test
    @DisplayName("Should successfully create a coupon and persist entity")
    void shouldCreateCouponSuccessfully() {
        CreateCouponRequest request = CreateCouponRequest.builder()
                .code("ABC-123")
                .description("Super Cupom")
                .discountValue(25.0)
                .expirationDate(futureDate)
                .published(true)
                .build();

        when(couponRepository.save(any(CouponEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CouponResponse response = couponService.create(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getCode()).isEqualTo("ABC123");
        assertThat(response.getDescription()).isEqualTo("Super Cupom");
        assertThat(response.getDiscountValue()).isEqualTo(25.0);
        assertThat(response.getStatus()).isEqualTo(CouponStatus.ACTIVE);
        assertThat(response.isPublished()).isTrue();
        assertThat(response.isRedeemed()).isFalse();

        ArgumentCaptor<CouponEntity> captor = ArgumentCaptor.forClass(CouponEntity.class);
        verify(couponRepository).save(captor.capture());
        CouponEntity saved = captor.getValue();
        assertThat(saved.getCode()).isEqualTo("ABC123");
    }

    @Test
    @DisplayName("Should return coupon response when found by ID")
    void shouldReturnCouponById() {
        UUID id = UUID.randomUUID();
        CouponEntity entity = CouponEntity.builder()
                .id(id)
                .code("ABC123")
                .description("Cupom Existente")
                .discountValue(10.0)
                .expirationDate(futureDate)
                .status(CouponStatus.ACTIVE)
                .published(false)
                .redeemed(false)
                .createdAt(Instant.now())
                .build();

        when(couponRepository.findById(id)).thenReturn(Optional.of(entity));

        CouponResponse response = couponService.getById(id);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getCode()).isEqualTo("ABC123");
        assertThat(response.getStatus()).isEqualTo(CouponStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when coupon not found by ID")
    void shouldThrowNotFoundWhenCouponMissing() {
        UUID id = UUID.randomUUID();
        when(couponRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.getById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    @DisplayName("Should soft delete active coupon and update status in repository")
    void shouldSoftDeleteActiveCoupon() {
        UUID id = UUID.randomUUID();
        CouponEntity entity = CouponEntity.builder()
                .id(id)
                .code("ABC123")
                .description("Cupom a Deletar")
                .discountValue(10.0)
                .expirationDate(futureDate)
                .status(CouponStatus.ACTIVE)
                .published(false)
                .redeemed(false)
                .createdAt(Instant.now())
                .build();

        when(couponRepository.findById(id)).thenReturn(Optional.of(entity));
        when(couponRepository.save(any(CouponEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        couponService.delete(id);

        ArgumentCaptor<CouponEntity> captor = ArgumentCaptor.forClass(CouponEntity.class);
        verify(couponRepository).save(captor.capture());
        CouponEntity updated = captor.getValue();

        assertThat(updated.getStatus()).isEqualTo(CouponStatus.DELETED);
        assertThat(updated.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should throw CouponAlreadyDeletedException when attempting to delete already deleted coupon")
    void shouldThrowExceptionWhenDeletingAlreadyDeleted() {
        UUID id = UUID.randomUUID();
        CouponEntity entity = CouponEntity.builder()
                .id(id)
                .code("ABC123")
                .description("Cupom Já Deletado")
                .discountValue(10.0)
                .expirationDate(futureDate)
                .status(CouponStatus.DELETED)
                .published(false)
                .redeemed(false)
                .createdAt(Instant.now())
                .deletedAt(Instant.now())
                .build();

        when(couponRepository.findById(id)).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> couponService.delete(id))
                .isInstanceOf(CouponAlreadyDeletedException.class)
                .hasMessageContaining(id.toString());
    }
}
