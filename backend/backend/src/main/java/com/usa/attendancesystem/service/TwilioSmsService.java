package com.usa.attendancesystem.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Service
@Primary
@ConditionalOnProperty(name = "twilio.enabled", havingValue = "true", matchIfMissing = false)
public class TwilioSmsService implements SmsService {

    private static final Logger log = LoggerFactory.getLogger(TwilioSmsService.class);

    private final String fromNumber;

    // Spring injects the properties from your application.properties file
    public TwilioSmsService(
            @Value("${twilio.account.sid}") String accountSid,
            @Value("${twilio.auth.token}") String authToken,
            @Value("${twilio.phone.number}") String fromNumber) {
        this.fromNumber = fromNumber;
        Twilio.init(accountSid, authToken);
        log.info("Twilio initialized with account SID: {}", accountSid);
    }

    @Override
    public void sendSms(String toPhoneNumber, String messageBody) {
        try {
            // NOTE: You might need to format the 'toPhoneNumber' to include the country code, e.g., "+91" for India.
            Message.creator(
                    new PhoneNumber(toPhoneNumber),
                    new PhoneNumber(fromNumber),
                    messageBody
            ).create();
            log.info("Successfully sent SMS to {}", toPhoneNumber);
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", toPhoneNumber, e.getMessage());
            // In a real app, you might add more robust error handling here.
        }
    }
}
