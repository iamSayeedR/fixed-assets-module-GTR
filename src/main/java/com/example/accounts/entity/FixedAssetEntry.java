package com.example.accounts.entity;

import com.example.accounts.entity.enums.DepreciationMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing a Fixed Asset Entry document
 * Used to activate a fixed asset and set initial depreciation parameters
 */
@Entity
@Table(name = "fixed_asset_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixedAssetEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entry_id")
    private Long entryId;

    @Column(name = "entry_number", unique = true, nullable = false, length = 50)
    private String entryNumber;

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fixed_asset_id", nullable = false)
    private FixedAsset fixedAsset;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Entry Details
    @Column(name = "initial_cost", nullable = false, precision = 19, scale = 4)
    private BigDecimal initialCost;

    @Column(name = "salvage_value", precision = 19, scale = 4)
    private BigDecimal salvageValue = BigDecimal.ZERO;

    @Column(name = "useful_life_months")
    private Integer usefulLifeMonths;

    @Column(name = "total_units")
    private Integer totalUnits;

    @Enumerated(EnumType.STRING)
    @Column(name = "depreciation_method", nullable = false, length = 50)
    private DepreciationMethod depreciationMethod;

    @Column(name = "depreciation_start_date", nullable = false)
    private LocalDate depreciationStartDate;

    // GL Accounts (can override asset defaults)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gl_account_id")
    private ChartOfAccount glAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "depreciation_gl_account_id")
    private ChartOfAccount depreciationGlAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_gl_account_id")
    private ChartOfAccount expenseGlAccount;

    // Additional Entry Details
    @Column(name = "expense_item", length = 200)
    private String expenseItem;

    @Column(name = "business_activity", length = 200)
    private String businessActivity;

    @Column(name = "category", length = 200)
    private String category;

    /**
     * Computed property: Depreciable Cost
     * This is the amount on which depreciation will be calculated
     * Formula: Initial Cost - Salvage Value
     */
    @Transient
    public BigDecimal getDepreciableCost() {
        if (initialCost == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal salvage = salvageValue != null ? salvageValue : BigDecimal.ZERO;
        return initialCost.subtract(salvage);
    }

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
