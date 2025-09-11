package br.com.joaobarbosa.modules.orders;

import br.com.joaobarbosa.modules.users.User;
import br.com.joaobarbosa.shared.value_objects.Money;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Builder
@Entity(name = "orders")
@Table(name = "orders")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, name = "user_id")
  private UUID userId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", insertable = false, updatable = false)
  private User user;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<OrderItem> items = new ArrayList<>();

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private OrderStatus status;

  @Column(name = "coupon_code")
  private String couponCode;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private Instant updatedAt;

  @Transient
  public Money getSubtotal() {
    return items.stream().map(OrderItem::getFinalPrice).reduce(Money.ZERO, Money::add);
  }

  @Transient
  public Money getDiscountTotal() {
    // TODO: Implementar lógica de cupom de desconto
    return Money.ZERO;
  }

  @Transient
  public Money getTotalAmount() {
    return getSubtotal().subtract(getDiscountTotal());
  }

  public void addItem(OrderItem item) {
    items.add(item);
    item.setOrder(this);
  }

  public void removeItem(OrderItem item) {
    if (item == null) return;
    items.remove(item);
    item.setOrder(null);
  }

  public boolean isPayable() {
    return this.status == OrderStatus.PENDING;
  }
}
