package com.example.accounts.entity;

import com.example.accounts.entity.enums.ParameterChangeType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing Changes of Fixed Asset Parameters/Reassessment
 * Tracks impairment, revaluation, and parameter changes
 */
@Entity
@Table(name = "fixed_asset_parameter_changes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixedAssetParameterChange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "change_id")
    private Long changeId;

    @Column(name = "change_number", unique = true, nullable = false, length = 50)
    private String changeNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fixed_asset_id", nullable = false)
    private FixedAsset fixedAsset;

    // Change Details
    @Column(name = "change_date", nullable = false)
    private LocalDate changeDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "change_type", nullable = false, length = 50)
    private ParameterChangeType changeType;

    @Column(name = "change_reason", nullable = false, columnDefinition = "TEXT")
    private String changeReason;

    // Old Values
    @Column(name = "old_gross_cost", precision = 19, scale = 4)
    private BigDecimal oldGrossCost;

    @Column(name = "old_salvage_value", precision = 19, scale = 4)
    private BigDecimal oldSalvageValue;

    @Column(name = "old_useful_life_months")
    private Integer oldUsefulLifeMonths;

    @Column(name = "old_accumulated_depreciation", precision = 19, scale = 4)
    private BigDecimal oldAccumulatedDepreciation;

    // New Values
    @Column(name = "new_gross_cost", precision = 19, scale = 4)
    private BigDecimal newGrossCost;

    @Column(name = "new_salvage_value", precision = 19, scale = 4)
    private BigDecimal newSalvageValue;

    @Column(name = "new_useful_life_months")
    private Integer newUsefulLifeMonths;

    // Adjustment Amount
    @Column(name = "adjustment_amount", precision = 19, scale = 4)
    private BigDecimal adjustmentAmount;

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
