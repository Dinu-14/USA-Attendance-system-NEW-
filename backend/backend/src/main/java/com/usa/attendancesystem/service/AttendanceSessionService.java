package com.usa.attendancesystem.service;

import com.usa.attendancesystem.dto.*;
import com.usa.attendancesystem.exception.DuplicateResourceException;
import com.usa.attendancesystem.exception.ResourceNotFoundException;
import com.usa.attendancesystem.model.*;
import com.usa.attendancesystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceSessionService {

    private final AttendanceSessionRepository sessionRepository;
    private final BatchRepository batchRepository;
    private final SubjectRepository subjectRepository;
    private final AdminRepository adminRepository;

    @Transactional
    public AttendanceSessionDto createSession(AttendanceSessionCreateRequest request, Authentication auth) {
        // Find batch and subject
        Batch batch = batchRepository.findById(request.batchId())
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with ID: " + request.batchId()));

        Subject subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + request.subjectId()));

        // Find admin creating the session
        String username = auth.getName();
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found: " + username));

        // Check if session already exists for this date/batch/subject
        sessionRepository.findByBatchAndSubjectAndDate(request.batchId(), request.subjectId(), request.sessionDate())
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("Attendance session already exists for this batch, subject and date");
                });

        // Create new session
        AttendanceSession session = new AttendanceSession(batch, subject, request.sessionDate(), admin);
        session = sessionRepository.save(session);

        // Convert to DTO
        return new AttendanceSessionDto(
                session.getId(),
                batch.getId(),
                String.valueOf(batch.getBatchYear()),
                subject.getId(),
                subject.getName(),
                session.getSessionDate(),
                session.isActive(),
                session.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<AttendanceSessionDto> getActiveSessions() {
        return sessionRepository.findAllActiveSessions()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AttendanceSessionDto> getTodaysActiveSessions() {
        return sessionRepository.findActiveSessionsByDate(LocalDate.now())
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public AttendanceSessionDto getSessionById(Long sessionId) {
        AttendanceSession session = sessionRepository.findActiveSessionById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Active session not found with ID: " + sessionId));
        return mapToDto(session);
    }

    @Transactional
    public void deactivateSession(Long sessionId) {
        AttendanceSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with ID: " + sessionId));

        session.setActive(false);
        sessionRepository.save(session);
    }

    private AttendanceSessionDto mapToDto(AttendanceSession session) {
        return new AttendanceSessionDto(
                session.getId(),
                session.getBatch().getId(),
                String.valueOf(session.getBatch().getBatchYear()),
                session.getSubject().getId(),
                session.getSubject().getName(),
                session.getSessionDate(),
                session.isActive(),
                session.getCreatedAt()
        );
    }
}
