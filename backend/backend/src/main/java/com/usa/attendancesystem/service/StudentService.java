package com.usa.attendancesystem.service;

import com. usa.attendancesystem.dto.*;
import com.usa.attendancesystem.exception.DuplicateResourceException;
import com.usa.attendancesystem.exception.ResourceNotFoundException;
import com.usa.attendancesystem.model.Batch;
import com.usa.attendancesystem.model.Student;
import com.usa.attendancesystem.model.Subject;
import com.usa.attendancesystem.repository.BatchRepository;
import com.usa.attendancesystem.repository.StudentRepository;
import com.usa.attendancesystem.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final BatchRepository batchRepository;
    private final SubjectRepository subjectRepository;

    @Transactional
    public StudentDto createStudent(CreateStudentRequest request) {
        studentRepository.findByStudentIdCode(request.studentIdCode()).ifPresent(s -> {
            throw new DuplicateResourceException("Student with ID code '" + request.studentIdCode() + "' already exists.");
        });

        Batch batch = batchRepository.findById(request.batchId())
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with ID: " + request.batchId()));

        Set<Subject> subjects = new HashSet<>(subjectRepository.findAllById(request.subjectIds()));
        if (subjects.size() != request.subjectIds().size()) {
            throw new ResourceNotFoundException("One or more subjects not found.");
        }

        Student student = Student.builder()
                .studentIdCode(request.studentIdCode())
                .fullName(request.fullName())
                .parentPhone(request.parentPhone())
                .studentPhone(request.studentPhone())
                .batch(batch)
                .subjects(subjects)
                .isActive(true)
                .build();

        Student savedStudent = studentRepository.save(student);
        return mapToStudentDto(savedStudent);
    }

    @Transactional(readOnly = true)
    public List<StudentDto> getFilteredStudents(Integer batchId, Integer subjectId) {
        return studentRepository.findActiveStudentsByBatchAndSubject(batchId, subjectId)
                .stream()
                .map(this::mapToStudentDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StudentDto getStudentById(UUID studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));
        return mapToStudentDto(student);
    }

    @Transactional
    public StudentDto updateStudent(UUID studentId, UpdateStudentRequest request) {
        Student studentToUpdate = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

        Batch batch = batchRepository.findById(request.batchId())
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with ID: " + request.batchId()));

        Set<Subject> subjects = new HashSet<>(subjectRepository.findAllById(request.subjectIds()));
        if (subjects.size() != request.subjectIds().size()) {
            throw new ResourceNotFoundException("One or more subjects not found.");
        }

        studentToUpdate.setFullName(request.fullName());
        studentToUpdate.setParentPhone(request.parentPhone());
        studentToUpdate.setStudentPhone(request.studentPhone());
        studentToUpdate.setBatch(batch);
        studentToUpdate.setSubjects(subjects);

        Student updatedStudent = studentRepository.save(studentToUpdate);
        return mapToStudentDto(updatedStudent);
    }

    @Transactional
    public void deactivateStudent(UUID studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));
        student.setActive(false);
        studentRepository.save(student);
    }

    // Helper method to convert Entity to DTO
    private StudentDto mapToStudentDto(Student student) {
        BatchDto batchDto = new BatchDto(student.getBatch().getId(), student.getBatch().getBatchYear());
        Set<SubjectDto> subjectDtos = student.getSubjects().stream()
                .map(subject -> new SubjectDto(subject.getId(), subject.getName()))
                .collect(Collectors.toSet());

        return new StudentDto(
                student.getId(),
                student.getStudentIdCode(),
                student.getFullName(),
                student.getParentPhone(),
                student.getStudentPhone(),
                student.isActive(),
                batchDto,
                subjectDtos
        );
    }
}