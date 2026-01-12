package com.example.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Request DTO for Fixed Asset Conservation
 * Supports both Start and Cancellation transactions
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConservationRequest {

    private Long fixedAssetId;
    private String conservationNumber;
    private LocalDate conservationDate;
    private String transactionType; // "Start of Conservation" or "Cancellation of Conservation"
    private String entity;
    private String reason;
    private String responsible;

    // For Start of Conservation
    private LocalDate depreciationSuspensionDate;
    private LocalDate plannedEndDate;

    // For Cancellation
    private Long conservationIdToCancel;
    private String cancellationReason;

    private String comment;
    private String transactionRemarks;
}
