package co.swaadisht.swaadisht.Services;

import jakarta.servlet.http.HttpSession;

import java.util.Map;

public interface CouponService {
    boolean validateCoupon(String coupon);

    double calculateDiscount(String coupon, double cartTotal);
//    Map<String, Object> applyCoupon(String coupon, int id, HttpSession session);
}
