package br.com.joaobarbosa.shared.value_objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Embeddable
@Getter
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED) // exigência do JPA
@AllArgsConstructor(access = AccessLevel.PRIVATE) // usamos factories estáticas
public class Money implements Comparable<Money>, Serializable {

    // ====== Config ======
    public static final Currency DEFAULT_CURRENCY = Currency.getInstance("BRL");
    public static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_UP;
    public static final MathContext DEFAULT_MATH_CONTEXT = MathContext.DECIMAL64;
    public static final Money ZERO = Money.ofMajor(BigDecimal.ZERO, DEFAULT_CURRENCY);
    @Serial private static final long serialVersionUID = 1L;

    // ====== Estado persistido ======
    // Guardamos com até 6 casas para evitar perdas em cálculos; na exibição normalizamos.
    @Column(name = "money_value", precision = 19, scale = 6, nullable = false)
    BigDecimal amount;

    @Column(name = "money_currency_code", nullable = false, length = 3)
    String currencyCode;

    // ====== Factories ======
    public static Money ofMajor(BigDecimal majorUnits, Currency currency) {
        Objects.requireNonNull(majorUnits, "majorUnits");
        Objects.requireNonNull(currency, "currency");
        return new Money(majorUnits.stripTrailingZeros(), currency.getCurrencyCode());
    }

    public static Money ofMajor(BigDecimal majorUnits) {
        return ofMajor(majorUnits, DEFAULT_CURRENCY);
    }

    /** Cria a partir de "unidades menores" (centavos). */
    public static Money ofMinor(long minorUnits, Currency currency) {
        Objects.requireNonNull(currency, "currency");
        int scale = Math.max(currency.getDefaultFractionDigits(), 0);
        BigDecimal major = BigDecimal.valueOf(minorUnits, scale);
        return ofMajor(major, currency);
    }

    public static Money ofMinor(long minorUnits) {
        return ofMinor(minorUnits, DEFAULT_CURRENCY);
    }

    /** atalhos seguros */
    public static Money of(int value, Currency currency) {
        return ofMajor(BigDecimal.valueOf(value), currency);
    }

    public static Money of(int value) {
        return of(value, DEFAULT_CURRENCY);
    }

    public static Money of(long value, Currency currency) {
        return ofMajor(BigDecimal.valueOf(value), currency);
    }

    public static Money of(long value) {
        return of(value, DEFAULT_CURRENCY);
    }

    public static Money of(double value, Currency currency) {
        return ofMajor(new BigDecimal(Double.toString(value), DEFAULT_MATH_CONTEXT), currency);
    }

    public static Money of(double value) {
        return of(value, DEFAULT_CURRENCY);
    }

    public static Money zero(Currency currency) {
        return ofMajor(BigDecimal.ZERO, currency);
    }

    private static void validatePercent(BigDecimal percent) {
        if (percent == null
                || percent.compareTo(BigDecimal.ZERO) < 0
                || percent.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("percent deve estar no intervalo [0, 100]");
        }
    }

    private static Locale guessLocale(Currency currency) {
        return switch (currency.getCurrencyCode()) {
            case "USD" -> Locale.US;
            case "EUR" -> Locale.GERMANY;
            case "BRL" -> Locale.of("pt", "BR");
            default -> Locale.getDefault();
        };
    }

    // ====== Acesso derivado ======
    public Currency currency() {
        return Currency.getInstance(currencyCode);
    }

    /** Valor com a escala “oficial” da moeda (para exibição e gravação final). */
    public BigDecimal toOfficialScale() {
        int scale = Math.max(currency().getDefaultFractionDigits(), 0);
        return amount.setScale(scale, DEFAULT_ROUNDING);
    }

    public long toMinorUnits() {
        int scale = Math.max(currency().getDefaultFractionDigits(), 0);
        BigDecimal scaled = amount.setScale(scale, DEFAULT_ROUNDING);
        return scaled.movePointRight(scale).longValueExact();
    }

    public boolean isZero() {
        return amount.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isPositive() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isNegative() {
        return amount.compareTo(BigDecimal.ZERO) < 0;
    }

    // ====== Operações ======
    public Money plus(Money other) {
        ensureSameCurrency(other);
        return new Money(this.amount.add(other.amount, DEFAULT_MATH_CONTEXT), this.currencyCode)
                .withOfficialScale();
    }

    public Money minus(Money other) {
        ensureSameCurrency(other);
        return new Money(
                        this.amount.subtract(other.amount, DEFAULT_MATH_CONTEXT), this.currencyCode)
                .withOfficialScale();
    }

    public Money times(BigDecimal factor) {
        Objects.requireNonNull(factor, "factor");
        return new Money(this.amount.multiply(factor, DEFAULT_MATH_CONTEXT), this.currencyCode)
                .withOfficialScale();
    }

    public Money times(double factor) {
        return times(new BigDecimal(Double.toString(factor), DEFAULT_MATH_CONTEXT));
    }

    public Money divide(BigDecimal divisor) {
        Objects.requireNonNull(divisor, "divisor");
        return new Money(this.amount.divide(divisor, DEFAULT_MATH_CONTEXT), this.currencyCode)
                .withOfficialScale();
    }

    public Money divide(BigDecimal divisor, int scale, RoundingMode rounding) {
        Objects.requireNonNull(divisor, "divisor");
        return new Money(this.amount.divide(divisor, scale, rounding), this.currencyCode)
                .withOfficialScale();
    }

    public Money abs() {
        return isNegative() ? negate() : this;
    }

    public Money negate() {
        return new Money(this.amount.negate(), this.currencyCode);
    }

    public Money minZero() {
        return isNegative() ? zero(currency()) : this;
    }

    public Money maxZero() {
        return isPositive() ? this : zero(currency());
    }

    public boolean isLessThan(Money other) {
        ensureSameCurrency(other);
        return this.amount.compareTo(other.amount) < 0;
    }

    public boolean isLessThan(BigDecimal other) {
        Objects.requireNonNull(other, "other");
        return this.amount.compareTo(other) < 0;
    }

    public boolean isLessThan(double other) {
        return this.amount.compareTo(BigDecimal.valueOf(other)) < 0;
    }

    public boolean isGreaterThan(Money other) {
        ensureSameCurrency(other);
        return this.amount.compareTo(other.amount) > 0;
    }

    public boolean isGreaterThan(BigDecimal other) {
        Objects.requireNonNull(other, "other");
        return this.amount.compareTo(other) > 0;
    }

    public boolean isGreaterThan(double other) {
        return this.amount.compareTo(BigDecimal.valueOf(other)) > 0;
    }

    /** p% deste valor (ex.: p=15 -> 15% de amount). */
    public Money percentageOf(BigDecimal percent) {
        validatePercent(percent);
        return times(percent).divide(BigDecimal.valueOf(100));
    }

    /** Aplica desconto percentual (ex.: 10% -> amount * (1-0.10)). */
    public Money applyDiscount(BigDecimal percent) {
        validatePercent(percent);
        BigDecimal factor =
                BigDecimal.ONE.subtract(
                        percent.divide(BigDecimal.valueOf(100), DEFAULT_MATH_CONTEXT));
        return times(factor);
    }

    /** Rateia em n partes quase iguais (útil para dividir contas). */
    public List<Money> allocate(int parts) {
        if (parts <= 0) throw new IllegalArgumentException("parts deve ser > 0");
        long totalMinor = this.toMinorUnits();
        long base = totalMinor / parts;
        int remainder = (int) (totalMinor % parts);

        Currency c = currency();
        Money baseShare = Money.ofMinor(base, c);
        Money plusOne = Money.ofMinor(base + 1, c);

        return java.util.stream.IntStream.range(0, parts)
                .mapToObj(i -> i < remainder ? plusOne : baseShare)
                .toList();
    }

    /** Conversão de moeda com taxa explícita (responsabilidade do chamador). */
    public Money convertTo(Currency targetCurrency, BigDecimal rate) {
        Objects.requireNonNull(targetCurrency, "targetCurrency");
        Objects.requireNonNull(rate, "rate");
        if (rate.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("rate deve ser > 0");
        BigDecimal targetAmount = this.amount.multiply(rate, DEFAULT_MATH_CONTEXT);
        return Money.ofMajor(targetAmount, targetCurrency).withOfficialScale();
    }

    /** Ajusta internamente para a escala “oficial” da moeda. */
    public Money withOfficialScale() {
        return Money.ofMajor(this.toOfficialScale(), this.currency());
    }

    // ====== Comparable / toString ======
    @Override
    public int compareTo(Money other) {
        ensureSameCurrency(other);
        return this.amount.compareTo(other.amount);
    }

	@Override
	public String toString() {
		Locale locale = guessLocale(currency());
		DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
		symbols.setCurrencySymbol(this.currency().getSymbol(locale));

		DecimalFormat df = new DecimalFormat("¤ #,##0.00", symbols);
		return df.format(amount);
	}
    // ====== Helpers ======
    private void ensureSameCurrency(Money other) {
        if (!this.currencyCode.equals(other.currencyCode)) {
            throw new IllegalArgumentException(
                    "Moedas diferentes: %s vs %s".formatted(this.currencyCode, other.currencyCode));
        }
    }
}
