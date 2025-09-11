package br.com.joaobarbosa.modules.coupons;

public enum CouponType {
    PERCENT("PERCENT"),
    FIXED("FIXED");

    final String value;

    CouponType(String value) {
        this.value = value;
    }
}
