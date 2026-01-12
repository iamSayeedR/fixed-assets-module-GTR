package com.example.accounts.dto;

import com.example.accounts.entity.enums.ParameterChangeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for Fixed Asset Parameter Change
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParameterChangeResponse {

    private Long changeId;
    private String changeNumber;
    private LocalDate changeDate;
    private ParameterChangeType changeType;

    // Asset details
    private Long fixedAssetId;
    private String assetNumber;
    private String assetDescription;

    // Old values
    private BigDecimal oldGrossCost;
    private BigDecimal oldSalvageValue;
    private Integer oldUsefulLifeMonths;
    private BigDecimal oldAccumulatedDepreciation;

    // New values
    private BigDecimal newGrossCost;
    private BigDecimal newSalvageValue;
    private Integer newUsefulLifeMonths;
    private BigDecimal newAccumulatedDepreciation;

    // Change details
    private BigDecimal adjustmentAmount;
    private String reason;
    private String description;

    // Posting
    private Boolean isPosted;
    private LocalDateTime postedDate;
}
