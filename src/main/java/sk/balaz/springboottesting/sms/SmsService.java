package sk.balaz.springboottesting.sms;

import com.twilio.rest.api.v2010.account.Message.Status;

public interface SmsService {

    Status sendSms(String from,
                           String to,
                           String message);
}
