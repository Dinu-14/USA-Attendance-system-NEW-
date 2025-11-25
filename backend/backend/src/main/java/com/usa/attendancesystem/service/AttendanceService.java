package com.usa.attendancesystem.service;

import com.usa.attendancesystem.dto.*;
import com.usa.attendancesystem.exception.DuplicateResourceException;
import com.usa.attendancesystem.exception.ResourceNotFoundException;
import com.usa.attendancesystem.model.AttendanceRecord;
import com.usa.attendancesystem.model.Student;
import com.usa.attendancesystem.model.Subject;
import com.usa.attendancesystem.repository.AttendanceRecordRepository;
import com.usa.attendancesystem.repository.StudentRepository;
import com.usa.attendancesystem.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final AttendanceRecordRepository attendanceRepository;
    private final SmsService smsService;
    private final StudentService studentService; // Re-use the mapper from StudentService

    @Transactional
    public void markAttendance(AttendanceMarkRequest request) {
        // 1. Find student and subject
        Student student = studentRepository.findByStudentIdCode(request.studentIdCode())
                .orElseThrow(() -> new ResourceNotFoundException("Student with ID code '" + request.studentIdCode() + "' not found."));

        Subject subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + request.subjectId()));

        // 2. Perform validations
        if (!student.isActive()) {
            throw new IllegalStateException("Student account is not active.");
        }
        
        // FIX: Changed '!=' to '.equals()' for safe object comparison.
        if (!student.getBatch().getId().equals(request.batchId())) {
            throw new IllegalStateException("Student is not enrolled in the selected batch.");
        }
        
        boolean isEnrolledInSubject = student.getSubjects().stream().anyMatch(s -> s.getId().equals(request.subjectId()));
        if (!isEnrolledInSubject) {
            throw new IllegalStateException("Student is not enrolled in the selected subject.");
        }

        // 3. Check for duplicate attendance
        Instant startOfDay = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endOfDay = LocalDate.now().plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        if (attendanceRepository.hasStudentMarkedAttendanceToday(student.getId(), subject.getId(), startOfDay, endOfDay)) {
            throw new DuplicateResourceException("Attendance already marked for this student today.");
        }

        // 4. Create and save the record
        AttendanceRecord record = new AttendanceRecord();
        record.setStudent(student);
        record.setSubject(subject);
        record.setAttendanceTimestamp(Instant.now());
        attendanceRepository.save(record);

        // 5. Send SMS notification
        String checkInTime = ZonedDateTime.ofInstant(record.getAttendanceTimestamp(), ZoneId.systemDefault())
                                          .format(DateTimeFormatter.ofPattern("hh:mm a"));
        String message = String.format(
                "Dear Parent, %s has checked in for the %s class at %s.",
                student.getFullName(), subject.getName(), checkInTime
        );
        smsService.sendSms(student.getParentPhone(), message);
    }

    @Transactional(readOnly = true)
    public AttendanceReportDto getAttendanceReport(LocalDate date, Integer batchId, Integer subjectId) {
        // 1. Get all students who should be in the class
        List<Student> enrolledStudents = studentRepository.findActiveStudentsByBatchAndSubject(batchId, subjectId);

        // 2. Get all attendance records for that day
        Instant startOfDay = date.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endOfDay = date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        List<AttendanceRecord> presentRecords = attendanceRepository.findBySubjectAndDateRange(subjectId, startOfDay, endOfDay);

        Set<UUID> presentStudentIds = presentRecords.stream()
                .map(ar -> ar.getStudent().getId())
                .collect(Collectors.toSet());

        // 3. Map present students to DTO, including check-in time
        List<PresentStudentDto> presentStudentDtos = presentRecords.stream()
                .map(ar -> new PresentStudentDto(
                        ar.getStudent().getId(),
                        ar.getStudent().getStudentIdCode(),
                        ar.getStudent().getFullName(),
                        ar.getAttendanceTimestamp()
                ))
                .toList();

        // 4. Filter the enrolled list to find absent students and map to DTO
        List<StudentDto> absentStudentDtos = enrolledStudents.stream()
                .filter(student -> !presentStudentIds.contains(student.getId()))
                .map(studentService::mapToStudentDto) // This now works correctly
                .toList();

        return new AttendanceReportDto(presentStudentDtos, absentStudentDtos);
    }
}