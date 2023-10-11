package sk.balaz.springboottesting.payment;

import com.twilio.rest.api.v2010.account.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sk.balaz.springboottesting.customer.Customer;
import sk.balaz.springboottesting.customer.CustomerRepository;
import sk.balaz.springboottesting.payment.card.CardPaymentCharge;
import sk.balaz.springboottesting.payment.card.CardPaymentCharger;
import sk.balaz.springboottesting.sms.SmsService;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

class PaymentServiceTest {
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CardPaymentCharger cardPaymentCharger;
    @Mock
    private SmsService smsService;
    private PaymentService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new PaymentService(
                paymentRepository,
                customerRepository,
                cardPaymentCharger,
                smsService);
    }

    @Test
    void itShouldChargeCardAndSendSms() {
        // Given
        UUID customerId = UUID.randomUUID();
        PaymentRequest request = new PaymentRequest(
                new Payment(
                        null,
                        customerId,
                        new BigDecimal("100.00"),
                        Currency.USD,
                        "card123xx",
                        "Donation"
                )
        );

        String from = "Tomas";
        String to = "Anna";
        String message = "Hello Anna";

        // customer exists
        given(customerRepository.findById(customerId))
                .willReturn(Optional.of(mock(Customer.class)));

        // card is charged successfully
        given(cardPaymentCharger.chargeCard(
                request.getPayment().getSource(),
                request.getPayment().getAmount(),
                request.getPayment().getCurrency(),
                request.getPayment().getDescription()
        )).willReturn(new CardPaymentCharge(true));

        // sms is delivered
        given(smsService.sendSms(
                from,
                to,
                message))
                .willReturn(Message.Status.DELIVERED);

        // When
        underTest.chargeCard(customerId,request);

        // Then
        ArgumentCaptor<Payment> paymentArgumentCaptor =
                ArgumentCaptor.forClass(Payment.class);

        then(paymentRepository).should().save(paymentArgumentCaptor.capture());

        Payment paymentArgumentCaptorValue = paymentArgumentCaptor.getValue();
        assertThat(paymentArgumentCaptorValue)
                .usingRecursiveComparison()
                .ignoringFields("customerId")
                .isEqualTo(request.getPayment());

        assertThat(paymentArgumentCaptorValue.getCustomerId()).isEqualTo(customerId);

        ArgumentCaptor<String> fromArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> toArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageArgumentCaptor = ArgumentCaptor.forClass(String.class);

        then(smsService).should().sendSms(
                fromArgumentCaptor.capture(),
                toArgumentCaptor.capture(),
                messageArgumentCaptor.capture()
        );

        assertThat(fromArgumentCaptor.getValue()).isEqualTo(from);
        assertThat(toArgumentCaptor.getValue()).isEqualTo(to);
        assertThat(messageArgumentCaptor.getValue()).isEqualTo(message);
    }

    @Test
    void itShouldThrowWhenSmsIsNotDelivered() {
        // Given
        UUID customerId = UUID.randomUUID();
        PaymentRequest request = new PaymentRequest(
                new Payment(
                        null,
                        customerId,
                        new BigDecimal("100.00"),
                        Currency.USD,
                        "card123xx",
                        "Donation"
                )
        );
        String to = "Anna";

        // customer exists
        given(customerRepository.findById(customerId))
                .willReturn(Optional.of(mock(Customer.class)));

        // card is charged successfully
        given(cardPaymentCharger.chargeCard(
                request.getPayment().getSource(),
                request.getPayment().getAmount(),
                request.getPayment().getCurrency(),
                request.getPayment().getDescription()
        )).willReturn(new CardPaymentCharge(true));

        given(smsService.sendSms(
                "Tomas",
                to,
                "Hello Anna"))
                .willThrow(new IllegalStateException(String.format("Sms to %s has not been delivered", to)));

        // When
        // Then
        assertThatThrownBy(() -> underTest.chargeCard(customerId,request) )
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Sms to %s has not been delivered", to));
    }

    @Test
    void itShouldThrownWhenCardIsNotCharged() {
        // Given
        UUID customerId = UUID.randomUUID();
        PaymentRequest request = new PaymentRequest(
                new Payment(
                        null,
                        customerId,
                        new BigDecimal("100.00"),
                        Currency.USD,
                        "card123xx",
                        "Donation"
                )
        );

        // customer exists
        given(customerRepository.findById(customerId))
                .willReturn(Optional.of(mock(Customer.class)));

        // card is charged successfully
        given(cardPaymentCharger.chargeCard(
                request.getPayment().getSource(),
                request.getPayment().getAmount(),
                request.getPayment().getCurrency(),
                request.getPayment().getDescription()
        )).willReturn(new CardPaymentCharge(false));

        // When
        assertThatThrownBy(() -> underTest.chargeCard(customerId,request) )
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Card not debited for customer [%s] ",customerId));

        // Then
        then(paymentRepository).should(never()).save(any(Payment.class));
        then(smsService).shouldHaveNoInteractions();
    }

    @Test
    void itShouldThrownWhenCurrencyIsNotSupported() {
        // Given
        UUID customerId = UUID.randomUUID();
        PaymentRequest request = new PaymentRequest(
                new Payment(
                        null,
                        customerId,
                        new BigDecimal("100.00"),
                        Currency.EUR,
                        "card123xx",
                        "Donation"
                )
        );

        // customer exists
        given(customerRepository.findById(customerId))
                .willReturn(Optional.of(mock(Customer.class)));

        // When
        assertThatThrownBy(() -> underTest.chargeCard(customerId,request) )
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Currency [%s] not supported",request.getPayment().getCurrency()));

        // Then
        then(cardPaymentCharger).shouldHaveNoInteractions();
        then(paymentRepository).shouldHaveNoInteractions();
    }

    @Test
    void itShouldThrownWhenCustomerNotFound() {

        UUID customerId = UUID.randomUUID();

        // customer exists
        given(customerRepository.findById(customerId))
                .willReturn(Optional.empty());

        // When
        assertThatThrownBy(() -> underTest.chargeCard(customerId, new PaymentRequest(new Payment())))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(String.format("Customer with id [%s] not found",customerId));

        then(cardPaymentCharger).shouldHaveNoInteractions();
        then(paymentRepository).shouldHaveNoInteractions();
        then(paymentRepository).shouldHaveNoInteractions();
    }
}
