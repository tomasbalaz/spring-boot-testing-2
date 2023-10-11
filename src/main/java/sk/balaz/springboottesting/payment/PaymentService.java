package sk.balaz.springboottesting.payment;

import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.balaz.springboottesting.customer.Customer;
import sk.balaz.springboottesting.customer.CustomerRepository;
import sk.balaz.springboottesting.payment.card.CardPaymentCharge;
import sk.balaz.springboottesting.payment.card.CardPaymentCharger;
import sk.balaz.springboottesting.sms.SmsService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {
    private static final List<Currency> ACCEPTED_CURRENCIES = List.of(Currency.USD, Currency.GBP);
    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final CardPaymentCharger cardPaymentCharger;
    private final SmsService smsService;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository,
                          CustomerRepository customerRepository,
                          CardPaymentCharger cardPaymentCharger, SmsService smsService) {
        this.paymentRepository = paymentRepository;
        this.customerRepository = customerRepository;
        this.cardPaymentCharger = cardPaymentCharger;
        this.smsService = smsService;
    }

    public void chargeCard(UUID customerId, PaymentRequest request) {
        // 1. Does customer exist if not throw an exception
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if (customerOptional.isEmpty()) {
            throw new IllegalStateException(String.format("Customer with id [%s] not found",customerId));
        }

        // 2. Do we support currency if not throw an exception
        boolean isCurrencySupported = ACCEPTED_CURRENCIES.stream()
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

        String to = "Anna";
        Message.Status status = smsService.sendSms("Tomas", to, "Hello Anna");
        if (!Message.Status.DELIVERED.equals(status)) {
            throw new IllegalStateException(String.format("Sms to %s has not been delivered", to));
        }
    }
}
