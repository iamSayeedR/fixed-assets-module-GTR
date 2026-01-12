package com.example.accounts.dto;

import com.example.accounts.entity.enums.AssetStatus;
import com.example.accounts.entity.enums.DepreciationMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for creating/updating Fixed Asset
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixedAssetRequest {

    private String assetNumber;
    private String description;
    private String folder;

    // Classification
    private Long classId;
    private String category;

    // Location
    private Long entityId;
    private String location;
    private String department;

    // Linked Items
    private Long itemId;
    private Long expenseItemId;

    // Financial Details
    private BigDecimal initialCost;
    private BigDecimal salvageValue;

    // Depreciation Settings
    private DepreciationMethod depreciationMethod;
    private Integer usefulLifeMonths;
    private Integer totalUnits;

    // GL Account Mappings
    private Long glAccountId;
    private Long depreciationGlAccountId;
    private Long expenseGlAccountId;
    private Long heldForSaleGlAccountId;
    private Long constructionInProgressGlAccountId;
    private Long capitalImprovementsGlAccountId;

    // Dates
    private LocalDate acquisitionDate;

    // Status
    private AssetStatus status;
    private Boolean useScheduling;
}
