package br.com.joaobarbosa.shared.value_objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {
  @Test
  @DisplayName("Deve criar um objeto Money corretamente")
  void testCreateMoney() {
    Money money = new Money(10, "USD");
    assertEquals(10, money.getValue().intValue());
    assertEquals("USD", money.getCurrencyCode().getCurrencyCode());
  }

  @Test
  @DisplayName("Deve retornar o valor formatado corretamente")
  void testToString() {
    Money money = new Money(10.5, "BRL");
    assertEquals("R$ 10,50", money.toString());
  }

  @Test
  @DisplayName("Deve somar um valor ao outro corretamente")
  void testAddMoney() {
    Money money1 = new Money(10);
    Money money2 = new Money(5);
    Money result = money1.add(money2);
    assertEquals(15, result.getValue().intValue());
    assertEquals("BRL", result.getCurrencyCode().getCurrencyCode());
  }

  @Test
  @DisplayName("Deve lançar exceção ao somar valores com moedas diferentes")
  void testAddMoneyDifferentCurrencies() {
    Money money1 = new Money(10, "USD");
    Money money2 = new Money(5, "BRL");
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              money1.add(money2);
            });
    assertEquals("Não é possível somar valores com moedas diferentes.", exception.getMessage());
  }

  @Test
  @DisplayName("Deve subtrair um valor do outro corretamente")
  void testSubtractMoney() {
    Money money1 = new Money(10);
    Money money2 = new Money(5);
    Money result = money1.subtract(money2);
    assertEquals(5, result.getValue().intValue());
    assertEquals("BRL", result.getCurrencyCode().getCurrencyCode());
  }

  @Test
  @DisplayName("Deve lançar exceção ao subtrair valores com moedas diferentes")
  void testSubtractMoneyDifferentCurrencies() {
    Money money1 = new Money(10, "USD");
    Money money2 = new Money(5, "BRL");
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              money1.subtract(money2);
            });
    assertEquals("Não é possível subtrair valores com moedas diferentes.", exception.getMessage());
  }

  @Test
  @DisplayName("Deve multiplicar o valor corretamente")
  void testMultiplyMoney() {
    Money money = new Money(10);
    Money result = money.multiply(new BigDecimal("2.5"));
    assertEquals(25, result.getValue().intValue());
    assertEquals("BRL", result.getCurrencyCode().getCurrencyCode());
  }

  @Test
  @DisplayName("Deve retornar zero ao aplicar minZero em valor negativo")
  void testMinZeroNegative() {
    Money money = new Money(-10);
    Money result = money.minZero();
    assertEquals(0, result.getValue().intValue());
  }

  @Test
  @DisplayName("Deve retornar o mesmo valor ao aplicar minZero em valor positivo")
  void testMinZeroPositive() {
    Money money = new Money(10);
    Money result = money.minZero();
    assertEquals(10, result.getValue().intValue());
  }
}
