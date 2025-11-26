package com.usa.attendancesystem.controller;

import com.usa.attendancesystem.dto.BroadcastMessageRequest;
import com.usa.attendancesystem.service.ParentMessagingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/messaging")
@RequiredArgsConstructor
public class ParentMessagingController {

    private final ParentMessagingService parentMessagingService;

    @PostMapping("/broadcast")
    public ResponseEntity<Void> sendBroadcast(@Valid @RequestBody BroadcastMessageRequest request) {
        parentMessagingService.sendBroadcastMessage(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/fee-reminders")
    public ResponseEntity<Map<String, String>> sendFeeReminders() {
        int remindersSent = parentMessagingService.sendFeeReminders();
        String message = String.format("Successfully sent %d fee reminders.", remindersSent);
        return ResponseEntity.ok(Map.of("message", message));
    }
}