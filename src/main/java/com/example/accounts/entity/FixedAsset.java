package com.example.accounts.entity;

import com.example.accounts.entity.enums.AssetStatus;
import com.example.accounts.entity.enums.DepreciationMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing a Fixed Asset
 * Tracks the complete lifecycle from acquisition to disposal
 */
@Entity
@Table(name = "fixed_assets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixedAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fixed_asset_id")
    private Long fixedAssetId;

    @Column(name = "asset_number", unique = true, length = 50)
    private String assetNumber;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "folder")
    private String folder;

    // Classification
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private FixedAssetClass assetClass;

    @Column(name = "category", length = 100)
    private String category;

    // Location & Entity
    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "location")
    private String location;

    @Column(name = "department", length = 100)
    private String department;

    // Linked Items
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_item_id")
    private ExpenseItem expenseItem;

    // Financial Details
    @Column(name = "initial_cost", nullable = false, precision = 19, scale = 4)
    private BigDecimal initialCost = BigDecimal.ZERO;

    @Column(name = "cost_adjustment", precision = 19, scale = 4)
    private BigDecimal costAdjustment = BigDecimal.ZERO;

    @Column(name = "accumulated_depreciation", precision = 19, scale = 4)
    private BigDecimal accumulatedDepreciation = BigDecimal.ZERO;

    @Column(name = "salvage_value", precision = 19, scale = 4)
    private BigDecimal salvageValue = BigDecimal.ZERO;

    // Computed: Gross Cost = Initial Cost + Cost Adjustment
    @Transient
    public BigDecimal getGrossCost() {
        return initialCost.add(costAdjustment);
    }

    // Computed: Net Book Value = Gross Cost - Accumulated Depreciation
    @Transient
    public BigDecimal getNetBookValue() {
        return getGrossCost().subtract(accumulatedDepreciation);
    }

    // Depreciation Settings
    @Enumerated(EnumType.STRING)
    @Column(name = "depreciation_method", nullable = false, length = 50)
    private DepreciationMethod depreciationMethod;

    @Column(name = "useful_life_months")
    private Integer usefulLifeMonths;

    @Column(name = "total_units")
    private Integer totalUnits;

    @Column(name = "remaining_units")
    private Integer remainingUnits;

    // Depreciation Tracking (Dynamic fields)
    @Column(name = "depreciation_start_date")
    private LocalDate depreciationStartDate;

    @Column(name = "last_depreciation_date")
    private LocalDate lastDepreciationDate;

    @Column(name = "last_depreciation_calculation_date")
    private LocalDateTime lastDepreciationCalculationDate;

    @Column(name = "next_depreciation_date")
    private LocalDate nextDepreciationDate;

    // GL Account Mappings (6 accounts)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gl_account_id", nullable = false)
    private ChartOfAccount glAccount; // Fixed Assets at Cost

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "depreciation_gl_account_id", nullable = false)
    private ChartOfAccount depreciationGlAccount; // Accumulated Depreciation

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_gl_account_id", nullable = false)
    private ChartOfAccount expenseGlAccount; // Depreciation Expense

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "held_for_sale_gl_account_id")
    private ChartOfAccount heldForSaleGlAccount; // Assets Held for Sale

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "construction_in_progress_gl_account_id")
    private ChartOfAccount constructionInProgressGlAccount; // Construction in Progress

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capital_improvements_gl_account_id")
    private ChartOfAccount capitalImprovementsGlAccount; // Capital Improvements

    // Dates
    @Column(name = "acquisition_date")
    private LocalDate acquisitionDate;

    @Column(name = "activation_date")
    private LocalDate activationDate;

    @Column(name = "disposal_date")
    private LocalDate disposalDate;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private AssetStatus status = AssetStatus.NEW;

    @Column(name = "use_scheduling")
    private Boolean useScheduling = false;

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
