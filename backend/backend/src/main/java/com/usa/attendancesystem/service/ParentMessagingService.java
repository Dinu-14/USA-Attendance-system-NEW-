package com.usa.attendancesystem.service;

import com.usa.attendancesystem.dto.BroadcastMessageRequest;
import com.usa.attendancesystem.model.FeeRecord;
import com.usa.attendancesystem.model.Student;
import com.usa.attendancesystem.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParentMessagingService {

    private final StudentRepository studentRepository;
    private final FeeManagementService feeManagementService;
    private final SmsService smsService;

    @Transactional(readOnly = true)
    public void sendBroadcastMessage(BroadcastMessageRequest request) {
        // This logic can be expanded. For now, we assume if subjectId is null, it's for the whole batch.
        List<Student> targetStudents = studentRepository.findActiveStudentsByBatchAndSubject(request.batchId(), request.subjectId());

        for (Student student : targetStudents) {
            smsService.sendSms(student.getParentPhone(), request.message());
        }
    }

    @Transactional(readOnly = true)
    public int sendFeeReminders() {
        List<FeeRecord> overdueRecords = feeManagementService.findOverdueFeeRecords();

        for (FeeRecord record : overdueRecords) {
            Student student = record.getStudent();
            String message = String.format(
                    "Dear Parent, this is a friendly reminder that a fee payment of $%.2f for %s is overdue. Please complete the payment at your earliest convenience. Thank you.",
                    record.getAmountDue().subtract(record.getAmountPaid()),
                    student.getFullName()
            );
            smsService.sendSms(student.getParentPhone(), message);
        }
        return overdueRecords.size(); // Return the count of reminders sent
    }
}