package br.com.joaobarbosa.modules.orders;

import br.com.joaobarbosa.shared.value_objects.Money;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "order_items")
@EqualsAndHashCode(of = "id")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "seat_id", nullable = false)
    private UUID seatId;

    @Column(name = "seat_label", nullable = false)
    private String seatLabel;

    @Column(name = "is_kind_half", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isKindHalf = false;

    @Embedded private Money unitPrice;

    @Transient
    public Money getFinalPrice() {
        if (unitPrice == null) {
            throw new NullPointerException("unitPrice não pode ser nulo ao calcular finalPrice");
        }
        return isKindHalf ? unitPrice.times(0.5) : unitPrice;
    }
}
