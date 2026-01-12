package com.example.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for Fixed Asset Sale
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixedAssetSaleResponse {

    private Long saleId;
    private String saleNumber;
    private LocalDate saleDate;

    // Asset details
    private Long fixedAssetId;
    private String assetNumber;
    private String assetDescription;
    private String content; // Additional description

    // Financial details
    private BigDecimal grossCost;
    private BigDecimal salvageValue;
    private BigDecimal depreciableCost;
    private BigDecimal accumulatedDepreciation;
    private BigDecimal currentDepreciation;
    private BigDecimal netBookValue;
    private BigDecimal salePrice;
    private BigDecimal gainLoss;

    // Buyer details
    private Long buyerId;
    private String buyerName;

    // Classification
    private String department;
    private String businessActivity;
    private String incomeItem;
    private String expenseItem;
    private String projectTask;

    // Posting
    private Boolean isPosted;
    private LocalDateTime postedDate;

    // Computed field
    public BigDecimal getGainLoss() {
        if (salePrice == null || netBookValue == null) {
            return BigDecimal.ZERO;
        }
        return salePrice.subtract(netBookValue);
    }
}
