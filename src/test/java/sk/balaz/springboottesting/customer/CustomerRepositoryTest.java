package sk.balaz.springboottesting.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


//https://assertj.github.io/doc/
@DataJpaTest(
        properties = "spring.jpa.properties.javax.persistence.validation.mode=none"
)
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository underTest;

    @Test
    void itShouldSelectCustomerByPhoneNumber() {
        // Given
        // When
        // Then
    }

    @Test
    void itShouldSaveCustomer() {
        // Given - set up of something
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, "Abel", "00000");

        // When - when something is called
        underTest.save(customer);

        // Then - assertion
        Optional<Customer> optionalCustomer = underTest.findById(id);
        assertThat(optionalCustomer)
                .isPresent()
                .hasValueSatisfying(c -> {
//                    assertThat(c.getId()).isEqualTo(id);
//                    assertThat(c.getName()).isEqualTo("Abel");
//                    assertThat(c.getPhoneNumber()).isEqualTo("111");
                    assertThat(c).isEqualToComparingFieldByField(customer);
                });

        assertThat(optionalCustomer.get())
                .usingRecursiveComparison()
                .isEqualTo(customer);
    }

    @Test
    void itShouldNotSaveCustomerWhenNameIsNull() {
        // Given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, "Abel", null);

        // When
        // Then
        assertThatThrownBy(() -> underTest.save(customer))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessage("not-null property references a null or transient value : " +
                        "sk.balaz.springboottesting.customer.Customer.phoneNumber");
    }

    @Test
    void itShouldNotSaveCustomerWhenPhoneNumberIsNull() {
        // Given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, null, "00000");

        // When
        // Then
        assertThatThrownBy(() -> underTest.save(customer))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessage("not-null property references a null or transient value : " +
                        "sk.balaz.springboottesting.customer.Customer.name");
    }
}