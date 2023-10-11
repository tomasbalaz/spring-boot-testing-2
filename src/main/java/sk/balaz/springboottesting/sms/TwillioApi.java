package sk.balaz.springboottesting.sms;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Service;

@Service
public class TwillioApi {

    public Message.Status create(String from, String to, String message) {
        return Message.creator(
                new PhoneNumber(from),
                new PhoneNumber(to),
                message
        ).create().getStatus();
    }
}
