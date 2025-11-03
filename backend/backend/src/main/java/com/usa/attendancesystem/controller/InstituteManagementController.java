package com.usa.attendancesystem.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.usa.attendancesystem.dto.BatchDto;
import com.usa.attendancesystem.dto.CreateBatchRequest;
import com.usa.attendancesystem.dto.CreateSubjectRequest;
import com.usa.attendancesystem.dto.SubjectDto;
import com.usa.attendancesystem.service.InstituteManagementService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for managing institute-level resources like Batches and Subjects.
 * These endpoints will be protected by security rules.
 */
@RestController
@RequestMapping("/api/admin/institute")
@RequiredArgsConstructor
public class InstituteManagementController {

    private final InstituteManagementService instituteManagementService;

    // --- Batch Endpoints ---

    @PostMapping("/batches")
    public ResponseEntity<BatchDto> createBatch(@Valid @RequestBody CreateBatchRequest request) {
        BatchDto createdBatch = instituteManagementService.createBatch(request);
        return new ResponseEntity<>(createdBatch, HttpStatus.CREATED);
    }

    @GetMapping("/batches")
    public ResponseEntity<List<BatchDto>> getAllBatches() {
        List<BatchDto> batches = instituteManagementService.getAllBatches();
        return ResponseEntity.ok(batches);
    }

    // --- Subject Endpoints ---

    @PostMapping("/subjects")
    public ResponseEntity<SubjectDto> createSubject(@Valid @RequestBody CreateSubjectRequest request) {
        SubjectDto createdSubject = instituteManagementService.createSubject(request);
        return new ResponseEntity<>(createdSubject, HttpStatus.CREATED);
    }

    @GetMapping("/subjects")
    public ResponseEntity<List<SubjectDto>> getAllSubjects() {
        List<SubjectDto> subjects = instituteManagementService.getAllSubjects();
        return ResponseEntity.ok(subjects);
    }
}