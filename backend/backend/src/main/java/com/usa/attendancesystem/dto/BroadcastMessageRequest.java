package com.usa.attendancesystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for sending a broadcast message to a targeted group of parents.
 */
public record BroadcastMessageRequest(
    @NotNull Integer batchId,
    Integer subjectId, // Optional: if null, send to all subjects in the batch
    @NotBlank String message
) {}