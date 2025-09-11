package br.com.joaobarbosa.modules.orders;

import static org.junit.jupiter.api.Assertions.*;

import br.com.joaobarbosa.modules.coupons.Coupon;
import br.com.joaobarbosa.modules.coupons.CouponType;
import br.com.joaobarbosa.shared.exceptions.client.BadRequestException;
import br.com.joaobarbosa.shared.value_objects.Money;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderTest {

    // ============================================================================
    // =                               HELPERS                                    =
    // ============================================================================
    private Order newPendingOrder() {
        return Order.builder().withId(UUID.randomUUID()).withStatus(OrderStatus.PENDING).build();
    }

    private OrderItem newItem(double value, boolean isHalf) {
        return OrderItem.builder()
                .seatId(UUID.randomUUID())
                .seatLabel("A1")
                .isKindHalf(isHalf)
                .unitPrice(Money.of(value))
                .build();
    }

    // ============================================================================
    // =                     A) SUBTOTAL / TOTAL BÁSICOS                          =
    // ============================================================================

    @Test
    @DisplayName("Deve retornar subtotal zero quando não há itens")
    void shouldReturnZeroSubtotalWhenNoItems() {
        Order order = newPendingOrder();
        assertTrue(order.getSubtotal().isZero());
    }

    @Test
    @DisplayName("Deve somar corretamente subtotal com itens inteiros e meia-entrada")
    void shouldSumCorrectSubtotalWithFullAndHalfItems() {
        Order order = newPendingOrder();

        OrderItem i1 = newItem(20, false);
        OrderItem i2 = newItem(30, true);

        order.addItem(i1);
        order.addItem(i2);

        Money expected = Money.of(35);

        assertEquals(expected, order.getSubtotal());
    }

    @Test
    @DisplayName("Deve retornar desconto zero por padrão")
    void shouldReturnZeroDiscountByDefault() {
        Order order = newPendingOrder();
        assertEquals(Money.ZERO, order.getDiscountTotal());
    }

    @Test
    @DisplayName("Deve calcular total como subtotal menos desconto")
    void shouldCalculateTotalAsSubtotalMinusDiscount() {
        Order order = newPendingOrder();
        order.addItem(newItem(100, false));

        assertEquals(order.getSubtotal(), order.getTotalAmount());
    }

    // ============================================================================
    // =                 B) DESCONTO: CÁLCULO E ARREDONDAMENTO                    =
    // ============================================================================

    @Test
    @DisplayName("Deve arredondar percentual com 2 casas decimais (HALF_UP)")
    void shouldRoundPercentageDiscountWithTwoDecimalsHalfUp() {
        Order o = newPendingOrder();
        o.addItem(newItem(33, false)); // 33.00
        Coupon c =
                Coupon.builder()
                        .type(CouponType.PERCENT)
                        .value(Money.of(10))
                        .isActive(true)
                        .build(); // 10% = 3.30
        o.applyCoupon(c);
        assertEquals(Money.of(3.30), o.getDiscountTotal());
        assertEquals(Money.of(29.70), o.getTotalAmount());
    }

    // ============================================================================
    // =                C) APLICAÇÃO DE CUPONS (COMPORTAMENTO)                    =
    // ============================================================================

    @Test
    @DisplayName("Deve aplicar cupom de valor fixo")
    void shouldApplyFixedCoupon() {
        OrderItem orderItem = newItem(10, false);
        OrderItem orderItem2 = newItem(20, false);
        Order order = newPendingOrder();
        order.addItem(orderItem);
        order.addItem(orderItem2); // Subtotal 20 + 10 = 30
        Coupon coupon =
                Coupon.builder()
                        .code("DESCONTO10")
                        .type(CouponType.FIXED)
                        .value(Money.of(10))
                        .isActive(true)
                        .build();
        order.applyCoupon(coupon);
        assertEquals("DESCONTO10", order.getCouponCode().get());
        assertEquals(0, order.getDiscountTotal().compareTo(Money.of(10)));
        assertEquals(0, order.getTotalAmount().compareTo(Money.of(20)));
    }

    @Test
    @DisplayName("Deve aplicar cupom percentual")
    void shouldApplyPercentageCoupon() {
        OrderItem orderItem = newItem(50, false);
        OrderItem orderItem2 = newItem(50, false);
        Order order = newPendingOrder();
        order.addItem(orderItem);
        order.addItem(orderItem2); // Subtotal 100
        Coupon coupon =
                Coupon.builder()
                        .code("DESCONTO10")
                        .type(CouponType.PERCENT)
                        .value(Money.of(10)) // 10%
                        .isActive(true)
                        .build();
        order.applyCoupon(coupon);
        assertEquals("DESCONTO10", order.getCouponCode().get());
        assertEquals(0, order.getDiscountTotal().compareTo(Money.of(10)));
        assertEquals(0, order.getTotalAmount().compareTo(Money.of(90)));
    }

    @Test
    @DisplayName("Deve substituir cupom anterior ao reaplicar")
    void shouldReplacePreviousCouponWhenReapplied() {
        Order o = newPendingOrder();
        o.addItem(newItem(100, false));
        Coupon c1 =
                Coupon.builder().type(CouponType.FIXED).value(Money.of(10)).isActive(true).build();
        Coupon c2 =
                Coupon.builder()
                        .type(CouponType.PERCENT)
                        .value(Money.of(20))
                        .isActive(true)
                        .build();
        o.applyCoupon(c1);
        o.applyCoupon(c2);
        assertEquals(0, o.getDiscountTotal().compareTo(Money.of(20)));
        assertEquals(0, o.getTotalAmount().compareTo(Money.of(80)));
    }

    @Test
    @DisplayName(
            "Deve recalcular desconto e total após adicionar itens com cupom percentual aplicado")
    void shouldRecalculateDiscountAndTotalWhenAddingItemsWithPercentCoupon() {
        Order o = newPendingOrder();
        o.addItem(newItem(100, false)); // subtotal 100
        Coupon c =
                Coupon.builder()
                        .type(CouponType.PERCENT)
                        .value(Money.of(10))
                        .isActive(true)
                        .build();
        o.applyCoupon(c); // desconto 10
        o.addItem(newItem(50, false)); // subtotal 150
        assertEquals(0, o.getDiscountTotal().compareTo(Money.of(15)));
        assertEquals(0, o.getTotalAmount().compareTo(Money.of(135)));
    }

    @Test
    @DisplayName("Deve ignorar cupom fixo de valor zero")
    void shouldIgnoreFixedCouponWithZeroValue() {
        Order o = newPendingOrder();
        o.addItem(newItem(40, false));
        Coupon c =
                Coupon.builder().type(CouponType.FIXED).value(Money.of(0)).isActive(true).build();
        o.applyCoupon(c);
        assertEquals(0, o.getTotalAmount().compareTo(Money.of(40)));
    }

    // ============================================================================
    // =                 D) REGRAS E VALIDAÇÕES DE CUPOM/STATUS                   =
    // ============================================================================

    @Test
    @DisplayName("Não deve aplicar cupom em pedido já pago")
    void shouldNotApplyCouponWhenOrderIsPaid() {
        Order order =
                Order.builder().withId(UUID.randomUUID()).withStatus(OrderStatus.PAID).build();
        Coupon coupon =
                Coupon.builder()
                        .code("DESCONTO10")
                        .type(CouponType.FIXED)
                        .value(Money.of(10))
                        .isActive(true)
                        .build();
        assertThrows(BadRequestException.class, () -> order.applyCoupon(coupon));
    }

    @Test
    @DisplayName("Não deve aplicar cupom em pedido cancelado")
    void shouldNotApplyCouponWhenOrderIsCancelled() {
        Order order =
                Order.builder().withId(UUID.randomUUID()).withStatus(OrderStatus.CANCELLED).build();
        Coupon coupon =
                Coupon.builder()
                        .code("DESCONTO10")
                        .type(CouponType.FIXED)
                        .value(Money.of(10))
                        .isActive(true)
                        .build();
        assertThrows(BadRequestException.class, () -> order.applyCoupon(coupon));
    }

    @Test
    @DisplayName("Deve retornar Optional.empty em getCouponCode quando não houver cupom")
    void shouldReturnEmptyOptionalWhenCouponIsNull() {
        Order order = newPendingOrder();
        assertTrue(order.getCouponCode().isEmpty());
    }

    @Test
    @DisplayName("Não deve aplicar cupom inativo")
    void shouldNotApplyInactiveCoupon() {
        Order order = newPendingOrder();
        Coupon coupon =
                Coupon.builder()
                        .code("DESCONTO10")
                        .type(CouponType.FIXED)
                        .value(Money.of(10))
                        .isActive(false)
                        .build();
        assertThrows(BadRequestException.class, () -> order.applyCoupon(coupon));
    }

    @Test
    @DisplayName("Não deve aplicar cupom fixo maior que o subtotal")
    void shouldNotApplyFixedCouponGreaterThanSubtotal() {
        Order order = newPendingOrder();
        order.addItem(newItem(5, false)); // Subtotal 5
        Coupon coupon =
                Coupon.builder()
                        .code("DESCONTO10")
                        .type(CouponType.FIXED)
                        .value(Money.of(10)) // 10 > 5
                        .isActive(true)
                        .build();
        assertThrows(BadRequestException.class, () -> order.applyCoupon(coupon));
    }

    @Test
    @DisplayName("Não deve aplicar cupom percentual maior que 100%")
    void shouldNotApplyPercentageCouponGreaterThan100() {
        Order order = newPendingOrder();
        order.addItem(newItem(100, false)); // Subtotal 100
        Coupon coupon =
                Coupon.builder()
                        .code("DESCONTO101")
                        .type(CouponType.PERCENT)
                        .value(Money.of(101)) // 101%
                        .isActive(true)
                        .build();
        assertThrows(BadRequestException.class, () -> order.applyCoupon(coupon));
    }

    // ============================================================================
    // =                        E) STATUS: PAGÁVEL OU NÃO                         =
    // ============================================================================

    @Test
    @DisplayName("Deve ser pagável apenas quando status for PENDING")
    void shouldBePayableOnlyWhenPending() {
        Order pending = newPendingOrder();
        assertTrue(pending.isPayable());

        Order paid = Order.builder().withId(UUID.randomUUID()).withStatus(OrderStatus.PAID).build();
        assertFalse(paid.isPayable());
    }

    // ============================================================================
    // =                 F) RELAÇÃO ITEM <-> ORDER (BACKREF)                       =
    // ============================================================================

    @Test
    @DisplayName("addItem deve sincronizar relação reversa")
    void shouldSyncBackReferenceWhenAddingItem() {
        Order order = newPendingOrder();
        OrderItem item = newItem(10, false);

        order.addItem(item);

        assertTrue(order.getItems().contains(item));
        assertSame(order, item.getOrder());
    }

    @Test
    @DisplayName("removeItem deve limpar relação reversa")
    void shouldClearBackReferenceWhenRemovingItem() {
        Order order = newPendingOrder();
        OrderItem item = newItem(15, false);

        order.addItem(item);
        order.removeItem(item);

        assertFalse(order.getItems().contains(item));
        assertNull(item.getOrder());
    }

    // ============================================================================
    // =                         G) TRATAMENTO DE NULOS                            =
    // ============================================================================

    @Test
    @DisplayName("Não deve permitir adicionar item nulo")
    void shouldNotAllowAddingNullItem() {
        Order order = newPendingOrder();
        assertThrows(NullPointerException.class, () -> order.addItem(null));
    }

    @Test
    @DisplayName("removeItem não deve quebrar ao receber nulo")
    void shouldNotThrowWhenRemovingNullItem() {
        Order order = newPendingOrder();
        assertDoesNotThrow(() -> order.removeItem(null));
    }
}
