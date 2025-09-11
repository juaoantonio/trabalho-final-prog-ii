package br.com.joaobarbosa.modules.coupons;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;


public enum CouponType {
	PERCENT("PERCENT"),
	FIXED("FIXED");

	final String value;

	CouponType(String value) {
		this.value = value;
	}
}
