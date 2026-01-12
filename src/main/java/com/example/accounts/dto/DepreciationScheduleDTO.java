package com.example.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for Depreciation Schedule Report
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepreciationScheduleDTO {

    private Long assetId;
    private String assetNumber;
    private String description;
    private String className;

    private BigDecimal grossCost;
    private BigDecimal salvageValue;
    private BigDecimal depreciableAmount;

    private Integer usefulLifeMonths;
    private Integer remainingLifeMonths;

    private BigDecimal accumulatedDepreciation;
    private BigDecimal netBookValue;

    private BigDecimal monthlyDepreciation;
    private BigDecimal annualDepreciation;

    private String depreciationMethod;
    private String lastDepreciationDate;
    private String nextDepreciationDate;
}
