package br.com.joaobarbosa.modules.coupons;

import br.com.joaobarbosa.modules.orders.Order;
import br.com.joaobarbosa.shared.value_objects.Money;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Builder
@Entity(name = "coupons")
@Table(name = "coupons")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Coupon {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, unique = true)
  private String code;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CouponType type;

  @Column(nullable = false)
  private Money value;

  @Column(nullable = false, name = "is_active")
  private Boolean isActive;

	@OneToOne(mappedBy = "coupon")
	private Order order;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private Instant updatedAt;
}
