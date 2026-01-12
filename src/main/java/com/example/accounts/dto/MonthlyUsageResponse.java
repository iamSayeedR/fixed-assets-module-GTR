package com.example.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for Fixed Asset Monthly Usage
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyUsageResponse {

    private Long usageId;
    private LocalDate usagePeriod;
    private LocalDate usageDate;

    // Asset details
    private Long fixedAssetId;
    private String assetNumber;
    private String assetDescription;

    // Usage details
    private Integer unitsUsed;
    private String uom;
    private Integer remainingUnits;
    private String notes;

    // Processing
    private Boolean isProcessed;
    private LocalDateTime processedDate;
}
