package com.example.accounts.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing Preparation for Fixed Asset Sale
 * Reclassifies asset as held for sale before actual disposal
 */
@Entity
@Table(name = "fixed_asset_sale_preparations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixedAssetSalePreparation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "preparation_id")
    private Long preparationId;

    @Column(name = "preparation_number", unique = true, nullable = false, length = 50)
    private String preparationNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fixed_asset_id", nullable = false)
    private FixedAsset fixedAsset;

    // Preparation Details
    @Column(name = "preparation_date", nullable = false)
    private LocalDate preparationDate;

    @Column(name = "expected_sale_date")
    private LocalDate expectedSaleDate;

    @Column(name = "expected_sale_price", precision = 19, scale = 4)
    private BigDecimal expectedSalePrice;

    // Reclassification Details
    @Column(name = "net_book_value_at_reclassification", nullable = false, precision = 19, scale = 4)
    private BigDecimal netBookValueAtReclassification;

    // Buyer Information (if known)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "potential_buyer_id")
    private Company potentialBuyer;

    // Posting (Reclassify to Held for Sale)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journal_entry_id")
    private JournalEntry journalEntry;

    @Column(name = "is_posted")
    private Boolean isPosted = false;

    @Column(name = "posted_date")
    private LocalDateTime postedDate;

    @Column(name = "posted_by", length = 100)
    private String postedBy;

    // Actual Sale (if completed)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actual_sale_id")
    private FixedAssetSale actualSale;

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
