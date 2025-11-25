package com.usa.attendancesystem.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.usa.attendancesystem.model.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {

    /**
     * Finds a student by their unique, human-readable ID code.
     * Used for validation to prevent duplicate student IDs.
     */
    Optional<Student> findByStudentIdCode(String studentIdCode);

    /**
     * Finds all active students belonging to a specific batch and enrolled in a specific subject.
     * This is the core query for filtering the student list in the admin dashboard.
     */
    @Query("SELECT s FROM Student s JOIN s.subjects sub WHERE s.batch.id = :batchId AND sub.id = :subjectId AND s.isActive = true")
    List<Student> findActiveStudentsByBatchAndSubject(@Param("batchId") Integer batchId, @Param("subjectId") Integer subjectId);
}