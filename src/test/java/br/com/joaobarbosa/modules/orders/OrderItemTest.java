/* (C)1 */
package br.com.joaobarbosa.modules.orders;

import static org.junit.jupiter.api.Assertions.*;

import br.com.joaobarbosa.shared.value_objects.Money;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderItemTest {

    @Test
    @DisplayName("Deve calcular preço final igual ao unitário quando não é meia")
    void finalPriceFull() {
        Money unit = new Money(20);
        OrderItem item =
                OrderItem.builder()
                        .seatId(UUID.randomUUID())
                        .seatLabel("A1")
                        .isKindHalf(false)
                        .unitPrice(unit)
                        .build();

        assertEquals(0, unit.getValue().compareTo(item.getFinalPrice().getValue()));
        assertEquals(unit.getCurrencyCode(), item.getFinalPrice().getCurrencyCode());
    }

    @Test
    @DisplayName("Deve calcular meia-entrada como metade do preço unitário")
    void finalPriceHalf() {
        Money unit = new Money(30);
        OrderItem item =
                OrderItem.builder()
                        .seatId(UUID.randomUUID())
                        .seatLabel("A2")
                        .isKindHalf(true)
                        .unitPrice(unit)
                        .build();

        Money expected = unit.multiply(new BigDecimal("0.5"));
        assertEquals(0, expected.getValue().compareTo(item.getFinalPrice().getValue()));
        assertEquals(expected.getCurrencyCode(), item.getFinalPrice().getCurrencyCode());
    }

    @Test
    @DisplayName("Deve lançar exceção se unitPrice for nulo ao calcular preço final")
    void finalPriceThrowsIfUnitPriceNull() {
        OrderItem item =
                OrderItem.builder()
                        .seatId(UUID.randomUUID())
                        .seatLabel("A3")
                        .isKindHalf(false)
                        .unitPrice(null)
                        .build();

        assertThrows(NullPointerException.class, item::getFinalPrice);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar calcular finalPrice com Money inconsistente")
    void finalPriceThrowsIfMoneyCurrencyInvalid() {
        Money unit = new Money(10, "USD");
        OrderItem item =
                OrderItem.builder()
                        .seatId(UUID.randomUUID())
                        .seatLabel("A4")
                        .isKindHalf(true)
                        .unitPrice(unit)
                        .build();

        assertDoesNotThrow(() -> item.getFinalPrice());
        assertEquals("USD", item.getFinalPrice().getCurrencyCode().getCurrencyCode());
    }
}
