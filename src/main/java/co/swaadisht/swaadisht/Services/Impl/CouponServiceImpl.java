package co.swaadisht.swaadisht.Services.Impl;

import co.swaadisht.swaadisht.Services.CouponService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CouponServiceImpl implements CouponService {
    @Override
    public boolean validateCoupon(String coupon) {
        return "VEDANT10".equalsIgnoreCase(coupon.trim());
    }

    @Override
    public double calculateDiscount(String coupon, double cartTotal) {
        if ("VEDANT10".equalsIgnoreCase(coupon.trim())) {
            return cartTotal * 0.1; // 10% discount
        }
        return 0;
    }
}
