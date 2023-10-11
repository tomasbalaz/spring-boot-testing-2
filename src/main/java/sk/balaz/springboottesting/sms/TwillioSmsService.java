package sk.balaz.springboottesting.sms;

import com.twilio.rest.api.v2010.account.Message.Status;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(
        value = "twillio.enabled",
        havingValue = "true"
)
public class TwillioSmsService implements SmsService {

    private final TwillioApi twillioApi;

    public TwillioSmsService(TwillioApi twillioApi) {
        this.twillioApi = twillioApi;
    }

    @Override
    public Status sendSms(String from,
                          String to,
                          String message) {

        return twillioApi.create(from, to, message);
    }
}
