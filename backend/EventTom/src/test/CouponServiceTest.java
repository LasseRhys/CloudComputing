package com.eventtom.backend.service;

import com.eventtom.backend.model.Coupon;
import com.eventtom.backend.repository.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;

    private Coupon validCoupon;
    private Coupon expiredCoupon;

    @BeforeEach
    void setUp() {
        validCoupon = new Coupon();
        validCoupon.setId(1L);
        validCoupon.setCode("SUMMER2025");
        validCoupon.setDiscountPercent(20);
        validCoupon.setExpiryDate(LocalDateTime.now().plusDays(30));
        validCoupon.setActive(true);
        validCoupon.setUsageCount(0);
        validCoupon.setMaxUsage(100);

        expiredCoupon = new Coupon();
        expiredCoupon.setId(2L);
        expiredCoupon.setCode("EXPIRED");
        expiredCoupon.setDiscountPercent(15);
        expiredCoupon.setExpiryDate(LocalDateTime.now().minusDays(1));
        expiredCoupon.setActive(true);
    }

    @Test
    void testValidateCoupon_Success() {
        when(couponRepository.findByCode("SUMMER2025")).thenReturn(Optional.of(validCoupon));

        Coupon result = couponService.validateCoupon("SUMMER2025");

        assertNotNull(result);
        assertEquals("SUMMER2025", result.getCode());
        assertEquals(20, result.getDiscountPercent());
        verify(couponRepository, times(1)).findByCode("SUMMER2025");
    }

    @Test
    void testValidateCoupon_NotFound() {
        when(couponRepository.findByCode(anyString())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            couponService.validateCoupon("INVALID");
        });
    }

    @Test
    void testValidateCoupon_Expired() {
        when(couponRepository.findByCode("EXPIRED")).thenReturn(Optional.of(expiredCoupon));

        assertThrows(IllegalStateException.class, () -> {
            couponService.validateCoupon("EXPIRED");
        });
    }

    @Test
    void testValidateCoupon_Inactive() {
        validCoupon.setActive(false);
        when(couponRepository.findByCode("SUMMER2025")).thenReturn(Optional.of(validCoupon));

        assertThrows(IllegalStateException.class, () -> {
            couponService.validateCoupon("SUMMER2025");
        });
    }

    @Test
    void testValidateCoupon_MaxUsageReached() {
        validCoupon.setUsageCount(100);
        when(couponRepository.findByCode("SUMMER2025")).thenReturn(Optional.of(validCoupon));

        assertThrows(IllegalStateException.class, () -> {
            couponService.validateCoupon("SUMMER2025");
        });
    }

    @Test
    void testApplyCoupon() {
        when(couponRepository.findByCode("SUMMER2025")).thenReturn(Optional.of(validCoupon));
        when(couponRepository.save(any(Coupon.class))).thenReturn(validCoupon);

        double originalPrice = 100.0;
        double discountedPrice = couponService.applyCoupon("SUMMER2025", originalPrice);

        assertEquals(80.0, discountedPrice, 0.01);
        verify(couponRepository).save(any(Coupon.class));
    }

    @Test
    void testCalculateDiscount() {
        double originalPrice = 100.0;
        int discountPercent = 20;

        double discountedPrice = couponService.calculateDiscount(originalPrice, discountPercent);

        assertEquals(80.0, discountedPrice, 0.01);
    }

    @Test
    void testCalculateDiscount_ZeroPercent() {
        double originalPrice = 100.0;
        int discountPercent = 0;

        double discountedPrice = couponService.calculateDiscount(originalPrice, discountPercent);

        assertEquals(100.0, discountedPrice, 0.01);
    }

    @Test
    void testCreateCoupon() {
        when(couponRepository.save(any(Coupon.class))).thenReturn(validCoupon);

        Coupon created = couponService.createCoupon(validCoupon);

        assertNotNull(created);
        assertEquals("SUMMER2025", created.getCode());
        verify(couponRepository, times(1)).save(validCoupon);
    }
}