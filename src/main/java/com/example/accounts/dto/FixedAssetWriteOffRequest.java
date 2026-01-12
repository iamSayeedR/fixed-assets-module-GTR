package com.example.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Request DTO for Fixed Asset Write-Off
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixedAssetWriteOffRequest {

    private Long fixedAssetId;
    private String writeOffNumber;
    private LocalDate writeOffDate;
    private String writeOffReason;

    // Classification
    private String department;
    private String businessActivity;
    private String expenseItem;

    // Additional details
    private String correspondence;
    private String entity;
    private String comment;
}
