package com.example.accounts.dto;

import com.example.accounts.entity.enums.ParameterChangeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for Fixed Asset Parameter Change
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParameterChangeRequest {

    private Long fixedAssetId;
    private String changeNumber;
    private LocalDate changeDate;
    private ParameterChangeType changeType;
    private String description;

    // Change details
    private BigDecimal adjustmentAmount;
    private Integer newUsefulLifeMonths;
    private BigDecimal newSalvageValue;
    private BigDecimal newAccumulatedDepreciation;

    private String reason;
}
