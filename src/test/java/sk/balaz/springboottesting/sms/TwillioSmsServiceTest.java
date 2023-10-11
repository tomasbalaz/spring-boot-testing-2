package sk.balaz.springboottesting.sms;

import com.twilio.exception.ApiConnectionException;
import com.twilio.rest.api.v2010.account.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

class TwillioSmsServiceTest {

    private TwillioSmsService underTest;
    @Mock
    private TwillioApi twillioApi;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new TwillioSmsService(twillioApi);
    }

    @Test
    void itShouldSendSms() {
        // Given
        String from = "Tomas";
        String to = "Anna";
        String message = "Hello Anna";

        given(twillioApi.create(anyString(), anyString(), anyString()))
                .willReturn(Message.Status.DELIVERED);

        // When
        Message.Status status = underTest.sendSms(from, to, message);

        // Then
        ArgumentCaptor<String> fromArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> toArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageArgumentCaptor = ArgumentCaptor.forClass(String.class);

        then(twillioApi).should().create(
                fromArgumentCaptor.capture(),
                toArgumentCaptor.capture(),
                messageArgumentCaptor.capture());

        assertThat(fromArgumentCaptor.getValue()).isEqualTo(from);
        assertThat(toArgumentCaptor.getValue()).isEqualTo(to);
        assertThat(messageArgumentCaptor.getValue()).isEqualTo(message);
        assertThat(status).isEqualTo(Message.Status.DELIVERED);
    }

    @Test
    void itShouldThrowWhenSmsNotDelivered() {
        // Given
        String from = "Tomas";
        String to = "Anna";
        String message = "Hello Anna";

        given(twillioApi.create(anyString(), anyString(), anyString()))
                .willThrow(new ApiConnectionException("can not deliver sms"));

        //When
        //Then
        assertThatThrownBy(() -> underTest.sendSms(from, to, message))
                .isInstanceOf(ApiConnectionException.class)
                .hasMessage("can not deliver sms");

        then(twillioApi).should().create(anyString(), anyString(), anyString());
        then(twillioApi).shouldHaveNoMoreInteractions();
    }
}