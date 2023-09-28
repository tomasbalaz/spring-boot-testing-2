package sk.balaz.springboottesting.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.balaz.springboottesting.customer.Customer;
import sk.balaz.springboottesting.customer.CustomerRepository;
import sk.balaz.springboottesting.payment.card.CardPaymentCharge;
import sk.balaz.springboottesting.payment.card.CardPaymentCharger;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final CardPaymentCharger cardPaymentCharger;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository,
                          CustomerRepository customerRepository,
                          CardPaymentCharger cardPaymentCharger) {
        this.paymentRepository = paymentRepository;
        this.customerRepository = customerRepository;
        this.cardPaymentCharger = cardPaymentCharger;
    }

    public void chargeCard(UUID customerId, PaymentRequest request) {
        // 1. Does customer exist if not throw an exception
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if (customerOptional.isEmpty()) {
            throw new IllegalStateException(String.format("Customer with id [%s] not found",customerId));
        }

        // 2. Do we support currency if not throw an exception
        boolean isCurrencySupported = Arrays.stream(Currency.values())
                .anyMatch(currency -> currency.equals(request.getPayment().getCurrency()));
        if(!isCurrencySupported) {
            throw new IllegalStateException(
                    String.format("Currency [%s] not supported",request.getPayment().getCurrency())
            );
        }

        // 3. Charge card
        CardPaymentCharge cardPaymentCharge = cardPaymentCharger.chargeCard(
                request.getPayment().getSource(),
                request.getPayment().getAmount(),
                request.getPayment().getCurrency(),
                request.getPayment().getDescription()
        );

        // 4. If not debited throw an exception
        if(!cardPaymentCharge.isCardDebited()) {
            throw new IllegalStateException(String.format("Card not debited for customer [%s] ",customerId));
        }

        // 5. Insert payment
        paymentRepository.save(request.getPayment());

        // 6. TODO: send sms

    }
}
