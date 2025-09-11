package br.com.joaobarbosa.shared.value_objects;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Currency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MoneyTest {

    // ============================================================================
    // =                               CRIAÇÃO                                    =
    // ============================================================================

    @Test
    @DisplayName("Deve criar um objeto Money corretamente")
    void shouldCreateMoney() {
        Money money = Money.of(10, Currency.getInstance("USD"));
        assertEquals(money, Money.of(10, Currency.getInstance("USD")));
        assertEquals("USD", money.getCurrencyCode());
    }

    @Test
    @DisplayName("Deve expor a constante ZERO com valor e moeda padrão (BRL)")
    void shouldExposeZeroConstantWithDefaultCurrency() {
        assertEquals(Money.ZERO, Money.of(0));
        assertEquals("BRL", Money.ZERO.getCurrencyCode());
    }

    // ============================================================================
    // =                          REPRESENTAÇÃO STRING                             =
    // ============================================================================

    @Test
    @DisplayName("Deve formatar BRL corretamente")
    void shouldFormatBrlCorrectly() {
        Money money = Money.of(10.5, Currency.getInstance("BRL"));
        assertEquals("R$ 10,50", money.toString());
    }

    @Test
    @DisplayName("Deve formatar USD corretamente")
    void shouldFormatUsdCorrectly() {
        Money money = Money.of(10.5, Currency.getInstance("USD"));
        assertEquals("$ 10.50", money.toString());
    }

    // ============================================================================
    // =                          OPERAÇÕES BÁSICAS                                =
    // ============================================================================

    @Test
    @DisplayName("Deve somar valores corretamente")
    void shouldAddMoney() {
        Money money1 = Money.of(10);
        Money money2 = Money.of(5);
        Money result = money1.plus(money2);
        assertEquals(result, Money.of(15));
        assertEquals("BRL", result.getCurrencyCode());
    }

    @Test
    @DisplayName("Não deve somar valores com moedas diferentes")
    void shouldNotAddMoneyWithDifferentCurrencies() {
        Money money1 = Money.of(10, Currency.getInstance("USD"));
        Money money2 = Money.of(5, Currency.getInstance("BRL"));
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> money1.plus(money2));
        assertEquals("Moedas diferentes: USD vs BRL", ex.getMessage());
    }

    @Test
    @DisplayName("Deve subtrair valores corretamente")
    void shouldSubtractMoney() {
        Money money1 = Money.of(10);
        Money money2 = Money.of(5);
        Money result = money1.minus(money2);
        assertEquals(Money.of(5), result);
        assertEquals("BRL", result.getCurrencyCode());
    }

    @Test
    @DisplayName("Não deve subtrair valores com moedas diferentes")
    void shouldNotSubtractMoneyWithDifferentCurrencies() {
        Money money1 = Money.of(10, Currency.getInstance("USD"));
        Money money2 = Money.of(5, Currency.getInstance("BRL"));
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> money1.minus(money2));
        assertEquals("Moedas diferentes: USD vs BRL", ex.getMessage());
    }

    @Test
    @DisplayName("Deve multiplicar por BigDecimal corretamente")
    void shouldMultiplyByBigDecimal() {
        Money money = Money.of(10);
        Money result = money.times(new BigDecimal("2.5"));
        //    assertEquals(0, result.compareTo(new BigDecimal("25.0")));
        assertEquals(Money.of(25.0), result);
        assertEquals("BRL", result.getCurrencyCode());
    }

    @Test
    @DisplayName("Deve multiplicar por fator numérico")
    void shouldMultiplyByNumericFactor() {
        Money base = Money.of(10, Currency.getInstance("USD"));
        BigDecimal factor = new BigDecimal("2.5");

        Money result = base.times(factor); // 10 * 2.5 = 25

        assertEquals(Money.of(25, Currency.getInstance("USD")), result);
        assertEquals(Currency.getInstance("USD"), result.currency());
    }

    // ============================================================================
    // =                           LIMITES E IMUTABILIDADE                         =
    // ============================================================================

    @Test
    @DisplayName("Deve retornar zero ao aplicar minZero em valor negativo")
    void shouldReturnZeroWhenMinZeroWithNegativeValue() {
        Money money = Money.of(-10);
        var result = money.minZero();
        assertEquals(Money.ZERO, result);
    }

    @Test
    @DisplayName("Deve manter mesmo valor ao aplicar minZero em valor positivo")
    void shouldKeepSameValueWhenMinZeroWithPositiveValue() {
        Money money = Money.of(10);
        Money result = money.minZero();
        assertEquals(money, result);
    }

    @Test
    @DisplayName("Operações devem preservar imutabilidade do objeto original")
    void shouldPreserveImmutability() {
        Money original = Money.of(100);
        Money addResult = original.plus(Money.of(10));
        Money subResult = original.minus(Money.of(5));
        Money mulResult = original.times(new BigDecimal("2"));

        assertEquals(Money.of(100), original);
        assertEquals(Money.of(110), addResult);
        assertEquals(Money.of(95), subResult);
        assertEquals(Money.of(200), mulResult);
    }

    // ============================================================================
    // =             DESCONTO PERCENTUAL: CÁLCULO, BORDAS, ARREDONDAMENTO         =
    // ============================================================================

    @Test
    @DisplayName("Deve calcular desconto percentual (BigDecimal) corretamente")
    void shouldCalculatePercentageDiscountFromBigDecimal() {
        Money price = Money.of(200); // BRL
        Money discount = price.percentageOf(new BigDecimal("10")); // 10% = 20.00

        assertEquals(Money.of(20), discount);
        assertEquals(Currency.getInstance("BRL"), discount.currency());
    }

    @Test
    @DisplayName("Não deve aceitar percentual negativo")
    void shouldNotAcceptNegativePercentage() {
        Money price = Money.of(100);
        assertThrows(
                IllegalArgumentException.class, () -> price.percentageOf(new BigDecimal("-1")));
    }

    @Test
    @DisplayName("Não deve aceitar percentual maior que 100")
    void shouldNotAcceptPercentageGreaterThan100() {
        Money price = Money.of(100);
        assertThrows(
                IllegalArgumentException.class, () -> price.percentageOf(new BigDecimal("100.01")));
    }

    @Test
    @DisplayName("Deve aceitar percentual igual a 0%")
    void shouldAcceptZeroPercent() {
        Money price = Money.of(100);
        Money discount = price.percentageOf(new BigDecimal("0"));

        assertEquals(Money.ZERO, discount);
    }

    @Test
    @DisplayName("Deve aceitar percentual igual a 100% e retornar valor total como desconto")
    void shouldAcceptHundredPercent() {
        Money price = Money.of(123);
        Money discount = price.percentageOf(new BigDecimal("100"));

        assertEquals(Money.of(123), discount);
    }

    @Test
    @DisplayName("Deve arredondar desconto percentual para 2 casas (HALF_UP)")
    void shouldRoundPercentageDiscountHalfUpToTwoDecimals() {
        Money price = Money.of(33);
        // 12.5% de 33 = 4.125 -> 4.13 (HALF_UP na escala oficial da moeda)
        Money discount = price.percentageOf(new BigDecimal("12.5"));

        assertEquals(Money.of(4.13), discount);
    }

    // ============================================================================
    // =                     IGUALDADE / HASHCODE POR VALOR                        =
    // ============================================================================

    @Test
    @DisplayName("equals/hashCode devem considerar valor e moeda")
    void shouldConsiderValueAndCurrencyInEqualsAndHashCode() {
        Money a = Money.of(10, Currency.getInstance("BRL"));
        Money b = Money.of(10, Currency.getInstance("BRL"));
        Money c = Money.of(10, Currency.getInstance("USD"));
        Money d = Money.of(11, Currency.getInstance("BRL"));

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, d);
    }
}
