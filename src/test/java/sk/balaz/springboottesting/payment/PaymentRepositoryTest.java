package sk.balaz.springboottesting.payment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(
        properties = "spring.jpa.properties.javax.persistence.validation.mode=none"
)
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository underTest;

    @Test
    void itShouldCreatePayment() {
        // Given
        long id = 1l;
        Payment payment = new Payment(
                id,
                UUID.randomUUID(),
                new BigDecimal("1.123"),
                Currency.GBP,
                "card123",
                "Donation"
        );

        // When
        underTest.save(payment);

        // Then
        Optional<Payment> paymentOptional = underTest.findById(id);
        assertThat(paymentOptional)
                .isPresent()
                .hasValueSatisfying(p -> {
                    assertThat(p.getId()).isEqualTo(id);
                });

        assertThat(paymentOptional.get())
                .usingRecursiveComparison()
                .isEqualTo(payment);

    }
}