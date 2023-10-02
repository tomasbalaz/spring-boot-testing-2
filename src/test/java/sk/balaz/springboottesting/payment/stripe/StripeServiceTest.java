package sk.balaz.springboottesting.payment.stripe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.balaz.springboottesting.payment.Currency;

import java.math.BigDecimal;

class StripeServiceTest {

    private StripeService underTest;

    @BeforeEach
    void setUp() {
        underTest = new StripeService();
    }

    @Test
    void itShouldChardCard() {
        // Given
        // When
        String cardSource = "0x0x0x";
        BigDecimal amount = new BigDecimal("100.0");
        Currency currency = Currency.USD;
        String description = "Visa";
        underTest.chargeCard(cardSource, amount, currency, description);
        // Then
    }
}
