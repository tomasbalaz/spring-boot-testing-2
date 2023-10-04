package sk.balaz.springboottesting.payment;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import sk.balaz.springboottesting.customer.Customer;
import sk.balaz.springboottesting.customer.CustomerRegistrationController;

import java.util.UUID;

@SpringBootTest
class PaymentIntegrationTest {

    // Do not autowire Controllers in Integration test.
    //@Autowired
    //private CustomerRegistrationController customerRegistrationController;

    @Test
    void itShouldCreatePayment() {
        // Given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(
                id,
                "James",
                "00000");

        // When
        // In this way REST api POST api/v1/customer-registration is not called, just method is called.
        //customerRegistrationController.registerNewCustomer(customer);

        // Then
    }
}
