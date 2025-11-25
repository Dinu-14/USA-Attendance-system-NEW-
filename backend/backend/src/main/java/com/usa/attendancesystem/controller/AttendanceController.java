package com.usa.attendancesystem.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.usa.attendancesystem.dto.AttendanceMarkRequest;
import com.usa.attendancesystem.dto.AttendanceReportDto;
import com.usa.attendancesystem.service.AttendanceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api") // Base path for both public and admin routes
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    /**
     * PUBLIC endpoint for the student check-in kiosk.
     * No authentication is required.
     */
    @PostMapping("/attendance/mark")
    public ResponseEntity<Void> markAttendance(@Valid @RequestBody AttendanceMarkRequest request) {
        attendanceService.markAttendance(request);
        return ResponseEntity.ok().build();
    }

    /**
     * ADMIN endpoint to view the daily attendance report.
     * This endpoint is protected by Spring Security.
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