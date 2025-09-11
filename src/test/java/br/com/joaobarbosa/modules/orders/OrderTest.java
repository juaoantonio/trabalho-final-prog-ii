/* (C)1 */
package br.com.joaobarbosa.modules.orders;

import static org.junit.jupiter.api.Assertions.*;

import br.com.joaobarbosa.shared.value_objects.Money;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderTest {

    /** ===== HELPERS ===== * */
    private Order newPendingOrder() {
        return Order.builder().userId(UUID.randomUUID()).status(OrderStatus.PENDING).build();
    }

    private OrderItem newItem(double value, boolean isHalf) {
        return OrderItem.builder()
                .seatId(UUID.randomUUID())
                .seatLabel("A1")
                .isKindHalf(isHalf)
                .unitPrice(new Money(value))
                .build();
    }

    /** ===== TESTES ===== * */
    @Test
    @DisplayName("Deve retornar subtotal zero quando não há itens")
    void subtotalEmptyOrder() {
        Order order = newPendingOrder();
        assertEquals(0, order.getSubtotal().getValue().compareTo(Money.ZERO.getValue()));
    }

    @Test
    @DisplayName("Deve somar corretamente o subtotal com itens inteiros e meia-entrada")
    void subtotalWithItems() {
        Order order = newPendingOrder();

        OrderItem i1 = newItem(20, false);
        OrderItem i2 = newItem(30, true);

        order.addItem(i1);
        order.addItem(i2);

        Money expected = new Money(35);

        assertEquals(0, expected.getValue().compareTo(order.getSubtotal().getValue()));
    }

    @Test
    @DisplayName("getDiscountTotal deve retornar zero por padrão")
    void discountTotalDefaultsToZero() {
        Order order = newPendingOrder();
        assertEquals(0, order.getDiscountTotal().getValue().compareTo(Money.ZERO.getValue()));
    }

    @Test
    @DisplayName("getTotalAmount deve ser subtotal - desconto")
    void totalAmountEqualsSubtotalMinusDiscount() {
        Order order = newPendingOrder();
        order.addItem(newItem(100, false));

        assertEquals(order.getSubtotal().getValue(), order.getTotalAmount().getValue());
    }

    @Test
    @DisplayName("addItem deve adicionar item e sincronizar relação reversa")
    void addItemSyncsBackref() {
        Order order = newPendingOrder();
        OrderItem item = newItem(10, false);

        order.addItem(item);

        assertTrue(order.getItems().contains(item));
        assertSame(order, item.getOrder());
    }

    @Test
    @DisplayName("removeItem deve remover item e limpar referência reversa")
    void removeItemClearsBackref() {
        Order order = newPendingOrder();
        OrderItem item = newItem(15, false);

        order.addItem(item);
        order.removeItem(item);

        assertFalse(order.getItems().contains(item));
        assertNull(item.getOrder());
    }

    @Test
    @DisplayName("isPayable deve ser verdadeiro apenas quando status for PENDING")
    void isPayableWhenPendingOnly() {
        Order pending = newPendingOrder();
        assertTrue(pending.isPayable());

        Order paid = Order.builder().userId(UUID.randomUUID()).status(OrderStatus.PAID).build();
        assertFalse(paid.isPayable());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar adicionar item nulo")
    void addNullItemThrowsException() {
        Order order = newPendingOrder();
        assertThrows(NullPointerException.class, () -> order.addItem(null));
    }

    @Test
    @DisplayName("removeItem com item nulo não deve quebrar")
    void removeNullItemDoesNothing() {
        Order order = newPendingOrder();
        assertDoesNotThrow(() -> order.removeItem(null));
    }
}
