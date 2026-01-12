package com.example.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Response DTO for Batch Depreciation Result
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchDepreciationResponse {

    private LocalDate period;
    private Integer totalAssetsProcessed;
    private Integer successCount;
    private Integer errorCount;
    private BigDecimal totalDepreciationAmount;
    private List<DepreciationItemResponse> depreciations;
    private List<String> errors;
}
