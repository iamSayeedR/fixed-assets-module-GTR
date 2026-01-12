package com.example.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for Capital Improvement Inventory Item
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CapitalImprovementInventoryItemDTO {

    private Long itemId;
    private Long improvementId;
    private Long sourceFixedAssetId;
    private String warehouse;
    private String itemCode;
    private String itemName;
    private String content;
    private String batchNumber;
    private BigDecimal quantity;
    private String uom;
    private BigDecimal unitCost;
    private BigDecimal amount;
    private Long glAccountId;
    private String glAccountCode;
    private String notes;

    // Computed field
    public BigDecimal getAmount() {
        if (amount != null) {
            return amount;
        }
        if (quantity != null && unitCost != null) {
            return quantity.multiply(unitCost);
        }
        return BigDecimal.ZERO;
    }
}
