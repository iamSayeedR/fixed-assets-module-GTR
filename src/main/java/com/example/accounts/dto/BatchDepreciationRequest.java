package com.example.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for Batch Depreciation Calculation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchDepreciationRequest {

    private LocalDate period;
    private String expenseItem;
    private String businessActivity;
    private String department;
    private Boolean manualProcessing = false;
    private String comment;
}
