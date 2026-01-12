package com.example.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for Sale Preparation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalePreparationRequest {

    private Long fixedAssetId;
    private String preparationNumber;
    private LocalDate preparationDate;
    private LocalDate expectedSaleDate;
    private BigDecimal expectedSalePrice;
    private Long potentialBuyerId;
    private String potentialBuyerName;
    private String saleReason;
    private String notes;
}
