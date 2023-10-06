package sk.balaz.springboottesting.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PhoneNumberValidatorTest {

    private PhoneNumberValidator underTest;

    @BeforeEach
    void setUp() {
        underTest = new PhoneNumberValidator();
    }

    @Test
    @DisplayName("Should validate phone number")
    void itShouldValidatePhoneNumber() {
        // Given
        String phoneNumber = "+4470000000";

        // When
        boolean isValid = underTest.test(phoneNumber);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should fail when length is bigger than 13")
    void itShouldNotValidatePhoneNumberWhenLength() {
        // Given
        String phoneNumber = "+44700000000";

        // When
        boolean isValid = underTest.test(phoneNumber);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should fail when does not start with '+'")
    void itShouldNotValidatePhoneNumberWhenDoesNotStart() {
        // Given
        String phoneNumber = "44700000000";

        // When
        boolean isValid = underTest.test(phoneNumber);

        // Then
        assertThat(isValid).isFalse();
    }
}
