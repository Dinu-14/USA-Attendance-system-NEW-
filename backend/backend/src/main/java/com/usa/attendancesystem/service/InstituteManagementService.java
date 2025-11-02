package com.usa.attendancesystem.service;

import com.usa.attendancesystem.dto.BatchDto;
import com..attendancesystem.dto.CreateBatchRequest;
import com.yourinstitute.attendancesystem.dto.CreateSubjectRequest;
import com.yourinstitute.attendancesystem.dto.SubjectDto;
import com.yourinstitute.attendancesystem.exception.DuplicateResourceException;
import com.yourinstitute.attendancesystem.model.Batch;
import com.yourinstitute.attendancesystem.model.Subject;
import com.yourinstitute.attendancesystem.repository.BatchRepository;
import com.yourinstitute.attendancesystem.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class containing business logic for managing Batches and Subjects.
 */
@Service
@RequiredArgsConstructor
public class InstituteManagementService {

    private final BatchRepository batchRepository;
    private final SubjectRepository subjectRepository;

    // --- Batch Methods ---

    @Transactional
    public BatchDto createBatch(CreateBatchRequest request) {
        batchRepository.findByBatchYear(request.batchYear()).ifPresent(b -> {
            throw new DuplicateResourceException("Batch with year " + request.batchYear() + " already exists.");
        });

        Batch newBatch = new Batch(request.batchYear());
        Batch savedBatch = batchRepository.save(newBatch);
        return new BatchDto(savedBatch.getId(), savedBatch.getBatchYear());
    }

    @Transactional(readOnly = true)
    public List<BatchDto> getAllBatches() {
        return batchRepository.findAll().stream()
                .map(batch -> new BatchDto(batch.getId(), batch.getBatchYear()))
                .collect(Collectors.toList());
    }

    // --- Subject Methods ---

    @Transactional
    public SubjectDto createSubject(CreateSubjectRequest request) {
        subjectRepository.findByName(request.name()).ifPresent(s -> {
            throw new DuplicateResourceException("Subject with name '" + request.name() + "' already exists.");
        });

        Subject newSubject = new Subject(request.name());
        Subject savedSubject = subjectRepository.save(newSubject);
        return new SubjectDto(savedSubject.getId(), savedSubject.getName());
    }

    @Transactional(readOnly = true)
    public List<SubjectDto> getAllSubjects() {
        return subjectRepository.findAll().stream()
                .map(subject -> new SubjectDto(subject.getId(), subject.getName()))
                .collect(Collectors.toList());
    }
}