package br.com.joaobarbosa.modules.orders;

import br.com.joaobarbosa.modules.coupons.Coupon;
import br.com.joaobarbosa.modules.users.User;
import br.com.joaobarbosa.shared.exceptions.client.BadRequestException;
import br.com.joaobarbosa.shared.value_objects.Money;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Builder(setterPrefix = "with")
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

    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "coupon_id", referencedColumnName = "id")
    private Coupon coupon;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Transient
    public Money getSubtotal() {
        return items.stream().map(OrderItem::getFinalPrice).reduce(Money.ZERO, Money::plus);
    }

    @Transient
    public Money getDiscountTotal() {
        if (coupon == null) return Money.ZERO;
        switch (this.coupon.getType()) {
            case FIXED -> {
                return coupon.getValue();
            }
            case PERCENT -> {
                return getSubtotal().percentageOf(coupon.getValue().toOfficialScale());
            }
            default -> {
                return Money.ZERO;
            }
        }
    }

    @Transient
    public Money getTotalAmount() {
        return getSubtotal().minus(getDiscountTotal());
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

    public void applyCoupon(Coupon coupon) {
        if (coupon.getIsActive() == false) {
            throw new BadRequestException("Cupom inativo.", "Verifique o cupom informado.");
        }
        if (this.status == OrderStatus.CANCELLED || this.status == OrderStatus.PAID) {
            throw new BadRequestException(
                    "Não é possível aplicar cupom em pedidos cancelados ou pagos.",
                    "Verifique o status do pedido.");
        }
        switch (coupon.getType()) {
            case FIXED -> {
                if (getSubtotal().minus(coupon.getValue()).isNegative()) {
                    throw new BadRequestException(
                            "O valor do cupom não pode ser maior que o subtotal do pedido.",
                            "Verifique o cupom informado.");
                }
            }
            case PERCENT -> {
                if (coupon.getValue().isGreaterThan(100)) {
                    throw new BadRequestException(
                            "O valor do cupom percentual não pode ser maior que 100%.",
                            "Verifique o cupom informado.");
                }
            }
            default -> {
                throw new BadRequestException(
                        "Tipo de cupom inválido.", "Verifique o cupom informado.");
            }
        }
        this.coupon = coupon;
    }

    public Optional<String> getCouponCode() {
        if (this.coupon == null) return Optional.empty();
        return Optional.of(coupon.getCode());
    }

    public boolean isPayable() {
        return this.status == OrderStatus.PENDING;
    }
}
