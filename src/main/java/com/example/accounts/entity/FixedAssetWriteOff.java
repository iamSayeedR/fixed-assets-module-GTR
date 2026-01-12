package com.example.accounts.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing Fixed Asset Write-Off
 * Records asset write-offs with loss calculation
 */
@Entity
@Table(name = "fixed_asset_write_offs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixedAssetWriteOff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "write_off_id")
    private Long writeOffId;

    @Column(name = "write_off_number", unique = true, nullable = false, length = 50)
    private String writeOffNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fixed_asset_id", nullable = false)
    private FixedAsset fixedAsset;

    // Write-Off Details
    @Column(name = "write_off_date", nullable = false)
    private LocalDate writeOffDate;

    @Column(name = "write_off_reason", nullable = false, columnDefinition = "TEXT")
    private String writeOffReason;

    // Financial Details at Write-Off
    @Column(name = "gross_cost_at_write_off", nullable = false, precision = 19, scale = 4)
    private BigDecimal grossCostAtWriteOff;

    @Column(name = "accumulated_depreciation_at_write_off", nullable = false, precision = 19, scale = 4)
    private BigDecimal accumulatedDepreciationAtWriteOff;

    @Column(name = "net_book_value_at_write_off", nullable = false, precision = 19, scale = 4)
    private BigDecimal netBookValueAtWriteOff;

    // Loss on Write-Off
    @Column(name = "loss_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal lossAmount;

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
