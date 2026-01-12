package com.example.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Request DTO for Fixed Asset Monthly Usage
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyUsageRequest {

    private Long fixedAssetId;
    private LocalDate usagePeriod;
    private LocalDate usageDate;
    private Integer unitsUsed;
    private String uom; // Unit of Measure (e.g., "days", "km", "hours")
    private String notes;
}
