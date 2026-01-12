package com.example.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for Fixed Asset Write-Off
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixedAssetWriteOffResponse {

    private Long writeOffId;
    private String writeOffNumber;
    private LocalDate writeOffDate;

    // Asset details
    private Long fixedAssetId;
    private String assetNumber;
    private String assetDescription;

    // Financial details
    private BigDecimal grossCost;
    private BigDecimal salvageValue;
    private BigDecimal depreciableCost;
    private BigDecimal accumulatedDepreciation;
    private BigDecimal currentDepreciation;
    private BigDecimal netBookValue;
    private BigDecimal lossOnWriteOff;

    // Classification
    private String department;
    private String businessActivity;
    private String expenseItem;

    // Additional details
    private String correspondence;
    private String entity;
    private String writeOffReason;
    private String comment;

    // Posting
    private Boolean isPosted;
    private LocalDateTime postedDate;
}
