package com.example.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for Fixed Asset Conservation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConservationResponse {

    private Long conservationId;
    private String conservationNumber;
    private LocalDate conservationDate;
    private String transactionType;

    // Asset details
    private Long fixedAssetId;
    private String assetNumber;
    private String assetDescription;

    // Conservation details
    private String entity;
    private String reason;
    private String responsible;
    private LocalDate depreciationSuspensionDate;
    private LocalDate plannedEndDate;

    // Financial snapshot at conservation
    private BigDecimal grossCostAtConservation;
    private BigDecimal salvageValueAtConservation;
    private BigDecimal accumulatedDepreciationAtConservation;
    private BigDecimal netBookValueAtConservation;
    private BigDecimal depreciableCostAtConservation;
    private Integer usefulLifeMonthsAtConservation;
    private String depreciationMethodAtConservation;

    // Cancellation details
    private Boolean isCancelled;
    private LocalDate cancellationDate;
    private String cancellationNumber;
    private String cancellationReason;

    // Posting
    private Boolean isPosted;
    private LocalDateTime postedDate;

    // Comments
    private String comment;
    private String transactionRemarks;

    // Computed field
    public BigDecimal getDepreciableCostAtConservation() {
        if (grossCostAtConservation == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal salvage = salvageValueAtConservation != null ? salvageValueAtConservation : BigDecimal.ZERO;
        return grossCostAtConservation.subtract(salvage);
    }
}
