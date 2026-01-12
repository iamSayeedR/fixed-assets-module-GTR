package com.example.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for Sale Preparation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalePreparationResponse {

    private Long preparationId;
    private String preparationNumber;
    private LocalDate preparationDate;

    // Asset details
    private Long fixedAssetId;
    private String assetNumber;
    private String assetDescription;
    private String className;

    // Financial details
    private BigDecimal grossCost;
    private BigDecimal accumulatedDepreciation;
    private BigDecimal netBookValueAtReclassification;

    // Sale details
    private LocalDate expectedSaleDate;
    private BigDecimal expectedSalePrice;
    private BigDecimal expectedGainLoss;

    // Buyer details
    private Long potentialBuyerId;
    private String potentialBuyerName;

    // Additional info
    private String saleReason;
    private String notes;

    // Posting
    private Boolean isPosted;
    private LocalDateTime postedDate;

    // Actual sale
    private Long actualSaleId;
    private Boolean isSold;
}
