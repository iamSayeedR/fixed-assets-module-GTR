package com.example.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for Fixed Asset Sale
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixedAssetSaleRequest {

    private Long fixedAssetId;
    private String saleNumber;
    private LocalDate saleDate;
    private BigDecimal salePrice;

    // Buyer details
    private Long buyerId;
    private String buyerName;
    private String buyerContact;

    // Sale details
    private String saleReason;
    private String paymentMethod;
    private String invoiceNumber;

    // Classification
    private String department;
    private String businessActivity;
    private String incomeItem;
    private String expenseItem;
    private String projectTask;

    private String notes;
}
