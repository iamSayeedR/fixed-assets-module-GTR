package com.example.accounts.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing Fixed Asset Depreciation
 * Tracks monthly depreciation calculations
 */
@Entity
@Table(name = "fixed_asset_depreciation", uniqueConstraints = @UniqueConstraint(name = "uk_asset_depreciation_period", columnNames = {
        "fixed_asset_id", "depreciation_period" }))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixedAssetDepreciation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "depreciation_id")
    private Long depreciationId;

    @Column(name = "depreciation_number", unique = true, length = 50)
    private String depreciationNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fixed_asset_id", nullable = false)
    private FixedAsset fixedAsset;

    // Period (YYYY-MM-01)
    @Column(name = "depreciation_period", nullable = false)
    private LocalDate depreciationPeriod;

    @Column(name = "depreciation_date", nullable = false)
    private LocalDate depreciationDate;

    // Opening Balances
    @Column(name = "opening_gross_cost", nullable = false, precision = 19, scale = 4)
    private BigDecimal openingGrossCost;

    @Column(name = "opening_accumulated_depreciation", nullable = false, precision = 19, scale = 4)
    private BigDecimal openingAccumulatedDepreciation;

    @Column(name = "opening_net_book_value", nullable = false, precision = 19, scale = 4)
    private BigDecimal openingNetBookValue;

    // Depreciation Amount
    @Column(name = "depreciation_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal depreciationAmount;

    // Closing Balances
    @Column(name = "closing_accumulated_depreciation", nullable = false, precision = 19, scale = 4)
    private BigDecimal closingAccumulatedDepreciation;

    @Column(name = "closing_net_book_value", nullable = false, precision = 19, scale = 4)
    private BigDecimal closingNetBookValue;

    // For Units of Production
    @Column(name = "units_used")
    private Integer unitsUsed;

    @Column(name = "depreciation_per_unit", precision = 19, scale = 4)
    private BigDecimal depreciationPerUnit;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

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

    // Audit fields
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
