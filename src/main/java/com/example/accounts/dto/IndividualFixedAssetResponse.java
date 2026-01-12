package com.example.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for Individual Fixed Asset Assignment
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndividualFixedAssetResponse {

    private Long assignmentId;
    private String assignmentNumber;
    private LocalDate assignmentDate;
    private String transactionType;

    // Asset details
    private Long fixedAssetId;
    private String assetNumber;
    private String assetDescription;

    // Employee details
    private String employeeId;
    private String employeeName;
    private String employeeDepartment;
    private String employeePosition;
    private String employeeEmail;

    // Assignment details
    private String purpose;
    private LocalDate expectedReturnDate;
    private LocalDate actualReturnDate;
    private String status;
    private String conditionAtAssignment;
    private String conditionAtReturn;

    // Responsibility
    private Boolean responsibilityAgreementSigned;
    private LocalDate agreementDate;
    private String witnessName;

    // Return details
    private String returnNotes;
    private Boolean damageReported;
    private String damageDescription;

    // Posting
    private Boolean isPosted;
    private LocalDateTime postedDate;

    private String notes;

    // Computed fields
    private Boolean isOverdue;
    private Integer daysAssigned;

    public Boolean getIsOverdue() {
        if (actualReturnDate != null) {
            return false; // Already returned
        }
        if (expectedReturnDate == null) {
            return false; // No expected return date
        }
        return LocalDate.now().isAfter(expectedReturnDate);
    }

    public Integer getDaysAssigned() {
        LocalDate endDate = actualReturnDate != null ? actualReturnDate : LocalDate.now();
        if (assignmentDate == null) {
            return 0;
        }
        return (int) java.time.temporal.ChronoUnit.DAYS.between(assignmentDate, endDate);
    }
}
