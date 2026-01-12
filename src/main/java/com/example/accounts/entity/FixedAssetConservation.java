package com.example.accounts.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing a Fixed Asset Conservation document
 * Used to temporarily pause depreciation on an asset
 */
@Entity
@Table(name = "fixed_asset_conservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixedAssetConservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conservation_id")
    private Long conservationId;

    @Column(name = "conservation_number", unique = true, nullable = false, length = 50)
    private String conservationNumber;

    @Column(name = "conservation_date", nullable = false)
    private LocalDate conservationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fixed_asset_id", nullable = false)
    private FixedAsset fixedAsset;

    @Column(name = "transaction_type", nullable = false, length = 50)
    private String transactionType; // "Start of Conservation" or "Cancellation of Conservation"

    @Column(name = "entity", length = 200)
    private String entity;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "responsible", length = 200)
    private String responsible;

    // Conservation period details
    @Column(name = "depreciation_suspension_date")
    private LocalDate depreciationSuspensionDate;

    @Column(name = "planned_end_date")
    private LocalDate plannedEndDate;

    // Financial snapshot at conservation start
    @Column(name = "gross_cost_at_conservation", precision = 19, scale = 4)
    private BigDecimal grossCostAtConservation;

    @Column(name = "salvage_value_at_conservation", precision = 19, scale = 4)
    private BigDecimal salvageValueAtConservation;

    @Column(name = "accumulated_depreciation_at_conservation", precision = 19, scale = 4)
    private BigDecimal accumulatedDepreciationAtConservation;

    @Column(name = "net_book_value_at_conservation", precision = 19, scale = 4)
    private BigDecimal netBookValueAtConservation;

    @Column(name = "useful_life_months_at_conservation")
    private Integer usefulLifeMonthsAtConservation;

    @Column(name = "depreciation_method_at_conservation", length = 50)
    private String depreciationMethodAtConservation;

    // Cancellation details
    @Column(name = "is_cancelled")
    private Boolean isCancelled = false;

    @Column(name = "cancellation_date")
    private LocalDate cancellationDate;

    @Column(name = "cancellation_number", length = 50)
    private String cancellationNumber;

    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

    // Posting
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journal_entry_id")
    private JournalEntry journalEntry;

    @Column(name = "is_posted")
    private Boolean isPosted = false;

    @Column(name = "posted_date")
    private LocalDateTime postedDate;

    @Column(name = "posted_by", length = 100)
    private String postedBy;

    // Comments
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "transaction_remarks", columnDefinition = "TEXT")
    private String transactionRemarks;

    // Audit fields
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
