package com.example.accounts.dto;

import com.example.accounts.entity.enums.AssetStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for Fixed Assets Summary/Dashboard
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixedAssetSummaryDTO {

    private Long totalAssets;
    private BigDecimal totalGrossCost;
    private BigDecimal totalAccumulatedDepreciation;
    private BigDecimal totalNetBookValue;

    // By Status
    private Long newAssets;
    private Long activeAssets;
    private Long fullyDepreciatedAssets;
    private Long heldForSaleAssets;
    private Long disposedAssets;
    private Long writtenOffAssets;

    // Financial Summary
    private BigDecimal totalSalvageValue;
    private BigDecimal averageDepreciationRate;

    // This Period
    private BigDecimal currentPeriodDepreciation;
    private Long assetsNeedingDepreciation;
}
