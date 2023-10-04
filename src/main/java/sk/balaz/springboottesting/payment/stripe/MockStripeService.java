package sk.balaz.springboottesting.payment.stripe;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import sk.balaz.springboottesting.payment.Currency;
import sk.balaz.springboottesting.payment.card.CardPaymentCharge;
import sk.balaz.springboottesting.payment.card.CardPaymentCharger;

import java.math.BigDecimal;

@Service
@ConditionalOnProperty(
        value = "stripe.enabled",
        havingValue = "false"
)
public class MockStripeService implements CardPaymentCharger {
    @Override
    public CardPaymentCharge chargeCard(String cardSource,
                                        BigDecimal amount,
                                        Currency currency,
                                        String description) {

        return new CardPaymentCharge(true);
    }
}
