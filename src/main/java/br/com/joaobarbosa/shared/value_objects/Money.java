package br.com.joaobarbosa.shared.value_objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Currency;
import java.util.Locale;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@Embeddable
@NoArgsConstructor
public class Money {
  private static final Currency DEFAULT_CURRENCY = Currency.getInstance("BRL");
  public static final Money ZERO = new Money(BigDecimal.ZERO, DEFAULT_CURRENCY);

	@Column(name = "money_value", precision = 19, scale = 2, nullable = false)
  private BigDecimal value;

	@Column(name = "money_currency_code", nullable = false, length = 3)
  private Currency currencyCode;

  public Money(Integer value, String currencyCode) {
    this.value = BigDecimal.valueOf(value);
    this.currencyCode = Currency.getInstance(currencyCode);
  }

  public Money(Double value, String currencyCode) {
    this.value = BigDecimal.valueOf(value);
    this.currencyCode = Currency.getInstance(currencyCode);
  }

  public Money(Integer value) {
    this.value = BigDecimal.valueOf(value);
    this.currencyCode = DEFAULT_CURRENCY;
  }

  public Money(Double value) {
    this.value = BigDecimal.valueOf(value);
    this.currencyCode = DEFAULT_CURRENCY;
  }

  public Money add(Money other) {
    if (!this.currencyCode.equals(other.currencyCode)) {
      throw new IllegalArgumentException("Não é possível somar valores com moedas diferentes.");
    }
    return new Money(this.value.add(other.value), this.currencyCode);
  }

  public Money subtract(Money other) {
    if (!this.currencyCode.equals(other.currencyCode)) {
      throw new IllegalArgumentException("Não é possível subtrair valores com moedas diferentes.");
    }
    return new Money(this.value.subtract(other.value), this.currencyCode);
  }

  public Money multiply(BigDecimal factor) {
    return new Money(this.value.multiply(factor), this.currencyCode);
  }

  public Money multiply(Money factor) {
    if (!this.currencyCode.equals(factor.currencyCode)) {
      throw new IllegalArgumentException(
          "Não é possível multiplicar valores com moedas diferentes.");
    }
    return new Money(this.value.multiply(factor.value), this.currencyCode);
  }

  public Money minZero() {
    if (this.value.compareTo(BigDecimal.ZERO) < 0) {
      return new Money(BigDecimal.ZERO, this.currencyCode);
    }
    return this;
  }

  @Override
  public String toString() {
    Locale locale = guessLocale(this.currencyCode);
    DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
    symbols.setCurrencySymbol(this.currencyCode.getSymbol(locale));

    DecimalFormat df = new DecimalFormat("¤ #,##0.00", symbols);
    return df.format(value);
  }

  private Locale guessLocale(Currency currency) {
    return switch (currency.getCurrencyCode()) {
      case "USD" -> Locale.US;
      case "EUR" -> Locale.GERMANY;
      case "BRL" -> Locale.of("pt", "BR");
      default -> Locale.getDefault();
    };
  }
}
