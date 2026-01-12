package com.example.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Response DTO for Individual Depreciation Item
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepreciationItemResponse {

    private Long depreciationId;
    private LocalDate depreciationPeriod;

    // Asset details
    private Long fixedAssetId;
    private String assetNumber;
    private String assetDescription;
    private String className;

    // Financial details
    private BigDecimal grossCost;
    private BigDecimal salvageValue;
    private BigDecimal openingAccumulatedDepreciation;
    private BigDecimal depreciationAmount;
    private BigDecimal closingAccumulatedDepreciation;
    private BigDecimal netBookValue;

    // Depreciation details
    private String depreciationMethod;
    private Integer usefulLifeMonths;
    private Integer remainingUsefulLifeMonths;
    private Integer unitsUsed;

    // Classification
    private String expenseItem;
    private String businessActivity;
    private String department;
    private String projectTask;

    // Posting
    private Boolean isPosted;
    private String comment;
}
