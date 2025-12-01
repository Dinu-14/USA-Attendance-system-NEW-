package com.usa.attendancesystem.service;

import com.usa.attendancesystem.dto.BatchDto;
import com.usa.attendancesystem.dto.CreateBatchRequest;
import com.usa.attendancesystem.dto.CreateSubjectRequest;
import com.usa.attendancesystem.dto.SubjectDto;
import com.usa.attendancesystem.exception.DuplicateResourceException;
import com.usa.attendancesystem.exception.ResourceNotFoundException;
import com.usa.attendancesystem.model.Batch;
import com.usa.attendancesystem.model.Subject;
import com.usa.attendancesystem.repository.BatchRepository;
import com.usa.attendancesystem.repository.SubjectRepository;
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

    @Transactional
    public SubjectDto updateSubject(Integer subjectId, CreateSubjectRequest request) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + subjectId));

        // Check if another subject with the same name already exists (excluding current subject)
        subjectRepository.findByName(request.name()).ifPresent(existingSubject -> {
            if (!existingSubject.getId().equals(subjectId)) {
                throw new DuplicateResourceException("Subject with name '" + request.name() + "' already exists.");
            }
        });

        subject.setName(request.name());
        Subject updatedSubject = subjectRepository.save(subject);
        return new SubjectDto(updatedSubject.getId(), updatedSubject.getName());
    }

    @Transactional
    public void deleteSubject(Integer subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + subjectId));

        // Note: In a real application, you might want to check if the subject is being used
        // by any students or in any attendance records before allowing deletion
        subjectRepository.delete(subject);
    }

    @Transactional
    public BatchDto updateBatch(Integer batchId, CreateBatchRequest request) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with ID: " + batchId));

        // Check if another batch with the same year already exists (excluding current batch)
        batchRepository.findByBatchYear(request.batchYear()).ifPresent(existingBatch -> {
            if (!existingBatch.getId().equals(batchId)) {
                throw new DuplicateResourceException("Batch with year " + request.batchYear() + " already exists.");
            }
        });

        batch.setBatchYear(request.batchYear());
        Batch updatedBatch = batchRepository.save(batch);
        return new BatchDto(updatedBatch.getId(), updatedBatch.getBatchYear());
    }

    @Transactional
    public void deleteBatch(Integer batchId) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with ID: " + batchId));

        // Note: In a real application, you might want to check if the batch is being used
        // by any students before allowing deletion
        batchRepository.delete(batch);
    }
}
