package com.example.accounts.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing Capital Improvements to Fixed Assets
 * Tracks improvements that increase asset cost and/or useful life
 */
@Entity
@Table(name = "fixed_asset_capital_improvements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixedAssetCapitalImprovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "improvement_id")
    private Long improvementId;

    @Column(name = "improvement_number", unique = true, nullable = false, length = 50)
    private String improvementNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fixed_asset_id", nullable = false)
    private FixedAsset fixedAsset;

    // Improvement Details
    @Column(name = "improvement_date", nullable = false)
    private LocalDate improvementDate;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "improvement_cost", nullable = false, precision = 19, scale = 4)
    private BigDecimal improvementCost;

    // Impact on Asset
    @Column(name = "extends_useful_life_months")
    private Integer extendsUsefulLifeMonths = 0;

    @Column(name = "increases_salvage_value", precision = 19, scale = 4)
    private BigDecimal increasesSalvageValue = BigDecimal.ZERO;

    // Invoice/Purchase Details
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Company supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @Column(name = "invoice_number", length = 100)
    private String invoiceNumber;

    // GL Account (can override default)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capital_improvements_gl_account_id")
    private ChartOfAccount capitalImprovementsGlAccount;

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
