package com.example.accounts.dto;

import com.example.accounts.entity.enums.AssetStatus;
import com.example.accounts.entity.enums.DepreciationMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for Fixed Asset
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixedAssetResponse {

    private Long fixedAssetId;
    private String assetNumber;
    private String description;
    private String folder;

    // Classification
    private Long classId;
    private String className;
    private String category;

    // Location
    private String location;
    private String department;

    // Financial Details
    private BigDecimal initialCost;
    private BigDecimal costAdjustment;
    private BigDecimal grossCost;
    private BigDecimal accumulatedDepreciation;
    private BigDecimal netBookValue;
    private BigDecimal salvageValue;

    // Depreciation Settings
    private DepreciationMethod depreciationMethod;
    private Integer usefulLifeMonths;
    private Integer totalUnits;
    private Integer remainingUnits;

    // Depreciation Tracking
    private LocalDate depreciationStartDate;
    private LocalDate lastDepreciationDate;
    private LocalDateTime lastDepreciationCalculationDate;
    private LocalDate nextDepreciationDate;

    // Status
    private AssetStatus status;

    // Dates
    private LocalDate acquisitionDate;
    private LocalDate activationDate;
    private LocalDate disposalDate;
}
