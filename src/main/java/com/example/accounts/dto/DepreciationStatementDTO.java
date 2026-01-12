package com.example.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for Statement of Depreciation Report
 * Shows depreciation details for all assets by department
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepreciationStatementDTO {

    // Asset identification
    private String department;
    private Long fixedAssetId;
    private String assetNumber;
    private String assetDescription;

    // Depreciation details
    private String depreciationMethod;

    // Financial details
    private BigDecimal initialCost;
    private BigDecimal salvageValue;
    private BigDecimal costAdjustment;
    private BigDecimal openingBalance;
    private BigDecimal depreciationChanges;
    private BigDecimal closingBalance;
    private BigDecimal netBookValue;

    // Computed fields
    public BigDecimal getGrossCost() {
        if (initialCost == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal adjustment = costAdjustment != null ? costAdjustment : BigDecimal.ZERO;
        return initialCost.add(adjustment);
    }

    public BigDecimal getDepreciableCost() {
        BigDecimal gross = getGrossCost();
        BigDecimal salvage = salvageValue != null ? salvageValue : BigDecimal.ZERO;
        return gross.subtract(salvage);
    }
}
