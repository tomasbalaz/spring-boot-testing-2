package sk.balaz.springboottesting.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import sk.balaz.springboottesting.customer.Customer;
import sk.balaz.springboottesting.customer.CustomerRegistrationRequest;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentIntegrationTest {

    // Do not autowire Controllers in Integration test.
    //@Autowired
    //private CustomerRegistrationController customerRegistrationController;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    void itShouldCreatePayment() throws Exception {
        // Given
        // customer
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(
                customerId,
                "James",
                "+4470000000");
        // ... Register request
        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(customer);

        // payment
        long paymentId = 1l;
        Payment payment = new Payment(
                paymentId,
                customerId,
                new BigDecimal("100.00"),
                Currency.GBP,
                "x0x0x0x0",
                "Donation"
        );
        // ... Payment request
        PaymentRequest paymentRequest = new PaymentRequest(payment);

        // When
        // Then
        // In this way REST api POST api/v1/customer-registration is not called, just method is called.
        //customerRegistrationController.registerNewCustomer(customer);

        mockMvc.perform(put("/api/v1/customer-registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(asJsonString(customerRegistrationRequest))))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/payment/{customerId}", customerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(asJsonString(paymentRequest))))
                .andDo(print())
                .andExpect(status().isOk());

        // Payment is stored in db
        // TODO: Do not use paymentRepository instead create an endpoint to retrieve payments for customers
        assertThat(paymentRepository.findById(paymentId))
                .isPresent()
                .hasValueSatisfying(p ->
                    assertThat(p.getId()).isEqualTo(paymentId)
                );

        // TODO: Ensure sms is delivered
    }

    private String asJsonString(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            fail("Failed convert object to json");
            return null;
        }
    }
}
