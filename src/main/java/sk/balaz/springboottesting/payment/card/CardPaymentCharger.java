package sk.balaz.springboottesting.payment.card;

import sk.balaz.springboottesting.payment.Currency;

import java.math.BigDecimal;

public interface CardPaymentCharger {

    CardPaymentCharge chargeCard(
            String cardSource,
            BigDecimal amount,
            Currency currency,
            String description
    );
}
