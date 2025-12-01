package com.usa.attendancesystem.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.usa.attendancesystem.dto.*;
import com.usa.attendancesystem.service.AttendanceService;
import com.usa.attendancesystem.service.AttendanceSessionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("") // No additional base path since context path already provides /api
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final AttendanceSessionService sessionService;

    // ============ SESSION MANAGEMENT ENDPOINTS (ADMIN) ============
    /**
     * ADMIN endpoint to create a new attendance session.
     */
    @PostMapping("/admin/attendance/sessions")
    public ResponseEntity<AttendanceSessionDto> createSession(
            @Valid @RequestBody AttendanceSessionCreateRequest request,
            Authentication auth) {
        AttendanceSessionDto session = sessionService.createSession(request, auth);
        return ResponseEntity.ok(session);
    }

    /**
     * ADMIN endpoint to get all active sessions.
     */
    @GetMapping("/admin/attendance/sessions")
    public ResponseEntity<List<AttendanceSessionDto>> getActiveSessions() {
        List<AttendanceSessionDto> sessions = sessionService.getActiveSessions();
        return ResponseEntity.ok(sessions);
    }

    /**
     * ADMIN endpoint to get today's active sessions.
     */
    @GetMapping("/admin/attendance/sessions/today")
    public ResponseEntity<List<AttendanceSessionDto>> getTodaysActiveSessions() {
        List<AttendanceSessionDto> sessions = sessionService.getTodaysActiveSessions();
        return ResponseEntity.ok(sessions);
    }

    /**
     * ADMIN endpoint to get a specific session.
     */
    @GetMapping("/admin/attendance/sessions/{sessionId}")
    public ResponseEntity<AttendanceSessionDto> getSession(@PathVariable Long sessionId) {
        AttendanceSessionDto session = sessionService.getSessionById(sessionId);
        return ResponseEntity.ok(session);
    }

    /**
     * ADMIN endpoint to deactivate a session.
     */
    @PutMapping("/admin/attendance/sessions/{sessionId}/deactivate")
    public ResponseEntity<Void> deactivateSession(@PathVariable Long sessionId) {
        sessionService.deactivateSession(sessionId);
        return ResponseEntity.ok().build();
    }

    // ============ ATTENDANCE MARKING ENDPOINTS ============
    /**
     * PUBLIC endpoint for marking attendance by index number in a session. No
     * authentication required - for student kiosk use.
     */
    @PostMapping("/attendance/mark-by-index")
    public ResponseEntity<Void> markAttendanceByIndex(@Valid @RequestBody AttendanceMarkByIndexRequest request) {
        attendanceService.markAttendanceByIndex(request);
        return ResponseEntity.ok().build();
    }

    /**
     * PUBLIC endpoint for the student check-in kiosk. No authentication is
     * required. (Legacy method - kept for backward compatibility)
     */
    @PostMapping("/attendance/mark")
    public ResponseEntity<Void> markAttendance(@Valid @RequestBody AttendanceMarkRequest request) {
        attendanceService.markAttendance(request);
        return ResponseEntity.ok().build();
    }

    /**
     * ADMIN endpoint to view the daily attendance report. This endpoint is
     * protected by Spring Security.
     */
    @GetMapping("/admin/attendance/report")
    public ResponseEntity<AttendanceReportDto> getAttendanceReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Integer batchId,
            @RequestParam Integer subjectId) {
        AttendanceReportDto report = attendanceService.getAttendanceReport(date, batchId, subjectId);
        return ResponseEntity.ok(report);
    }
}
