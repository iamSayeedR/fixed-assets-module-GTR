package com.example.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Request DTO for Individual Fixed Asset Assignment
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndividualFixedAssetRequest {

    private Long fixedAssetId;
    private String assignmentNumber;
    private LocalDate assignmentDate;
    private String transactionType; // "Transfer to employee" or "Return from employee"

    // Employee details
    private String employeeId;
    private String employeeName;
    private String employeeDepartment;
    private String employeePosition;
    private String employeeEmail;

    // Assignment details
    private String purpose;
    private LocalDate expectedReturnDate;
    private String conditionAtAssignment;

    // Responsibility
    private Boolean responsibilityAgreementSigned;
    private LocalDate agreementDate;
    private String witnessName;

    // For returns
    private LocalDate actualReturnDate;
    private String conditionAtReturn;
    private String returnNotes;
    private Boolean damageReported;
    private String damageDescription;

    private String notes;
}
