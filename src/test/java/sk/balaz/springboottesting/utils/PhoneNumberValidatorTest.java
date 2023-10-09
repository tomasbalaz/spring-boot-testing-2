package sk.balaz.springboottesting.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class PhoneNumberValidatorTest {

    private PhoneNumberValidator underTest;

    @BeforeEach
    void setUp() {
        underTest = new PhoneNumberValidator();
    }

    @ParameterizedTest
    @CsvSource({
            "+4470000000, true",
            "+44700000000, false",
            "44700000000, false",
    })
    @DisplayName("Should validate phone number")
    void itShouldValidatePhoneNumber(String phoneNumber, boolean expected) {
        // Given
        // When
        boolean isValid = underTest.test(phoneNumber);

        // Then
        assertThat(isValid).isEqualTo(expected);
    }
}
