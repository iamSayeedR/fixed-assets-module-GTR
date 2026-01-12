package com.example.accounts.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing Fixed Asset Monthly Usage
 * Used for Units of Production depreciation method
 */
@Entity
@Table(name = "fixed_asset_monthly_usage", uniqueConstraints = @UniqueConstraint(name = "uk_asset_usage_period", columnNames = {
        "fixed_asset_id", "usage_period" }))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixedAssetMonthlyUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usage_id")
    private Long usageId;

    @Column(name = "usage_number", unique = true, length = 50)
    private String usageNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fixed_asset_id", nullable = false)
    private FixedAsset fixedAsset;

    // Period (YYYY-MM-01)
    @Column(name = "usage_period", nullable = false)
    private LocalDate usagePeriod;

    @Column(name = "usage_date", nullable = false)
    private LocalDate usageDate;

    // Usage Details
    @Column(name = "units_used", nullable = false)
    private Integer unitsUsed;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Processing Status
    @Column(name = "is_processed")
    private Boolean isProcessed = false;

    @Column(name = "processed_date")
    private LocalDateTime processedDate;

    @Column(name = "processed_by", length = 100)
    private String processedBy;

    // Link to depreciation
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "depreciation_id")
    private FixedAssetDepreciation depreciation;

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
