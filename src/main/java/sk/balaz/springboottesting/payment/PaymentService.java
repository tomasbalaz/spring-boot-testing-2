package sk.balaz.springboottesting.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.balaz.springboottesting.customer.CustomerRepository;
import sk.balaz.springboottesting.payment.card.CardPaymentCharger;

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

    public void chargeCard(UUID userId, PaymentRequest request) {

    }
}
