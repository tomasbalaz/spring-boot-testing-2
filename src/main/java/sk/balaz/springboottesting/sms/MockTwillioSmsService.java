package sk.balaz.springboottesting.sms;

import com.twilio.rest.api.v2010.account.Message;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(
        value = "twillio.enabled",
        havingValue = "false"
)
public class MockTwillioSmsService implements SmsService {
    @Override
    public Message.Status sendSms(String from, String to, String message) {
        return Message.Status.DELIVERED;
    }
}
