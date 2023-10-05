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

import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.fail;
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

    @Test
    void itShouldCreatePayment() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(
                id,
                "James",
                "00000");
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        // When
        // Then
        // In this way REST api POST api/v1/customer-registration is not called, just method is called.
        //customerRegistrationController.registerNewCustomer(customer);

        mockMvc.perform(put("/api/v1/customer-registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(asJsonString(request))))
                .andDo(print())
                .andExpect(status().isOk());
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
