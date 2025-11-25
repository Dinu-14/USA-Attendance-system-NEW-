package com.usa.attendancesystem.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MockSmsService implements SmsService {

    private static final Logger log = LoggerFactory.getLogger(MockSmsService.class);

    @Override
    public void sendSms(String toPhoneNumber, String message) {
        // In a real production environment, this method would contain the logic
        // to call an external SMS Gateway API like Twilio, Vonage, or AWS SNS.
        
        log.info("--- MOCK SMS SENT ---");
        log.info("Recipient: {}", toPhoneNumber);
        log.info("Message: {}", message);
        log.info("---------------------");
    }
}