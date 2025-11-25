package com.usa.attendancesystem.dto;

import java.util.List;

/**
 * DTO to send the complete attendance report to the admin dashboard.
 * It clearly separates present and absent students.
 */
public record AttendanceReportDto(
    List<PresentStudentDto> presentStudents,
    List<StudentDto> absentStudents
) {}