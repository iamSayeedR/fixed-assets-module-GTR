package com.example.accounts.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing Fixed Asset Sale (Actual Disposal)
 * Records the actual sale/disposal of a fixed asset with gain/loss calculation
 */
@Entity
@Table(name = "fixed_asset_sales")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixedAssetSale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sale_id")
    private Long saleId;

    @Column(name = "sale_number", unique = true, nullable = false, length = 50)
    private String saleNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fixed_asset_id", nullable = false)
    private FixedAsset fixedAsset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preparation_id")
    private FixedAssetSalePreparation preparation;

    // Sale Details
    @Column(name = "sale_date", nullable = false)
    private LocalDate saleDate;

    @Column(name = "sale_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal salePrice;

    // Buyer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private Company buyer;

    // Financial Details at Sale
    @Column(name = "gross_cost_at_sale", nullable = false, precision = 19, scale = 4)
    private BigDecimal grossCostAtSale;

    @Column(name = "accumulated_depreciation_at_sale", nullable = false, precision = 19, scale = 4)
    private BigDecimal accumulatedDepreciationAtSale;

    @Column(name = "net_book_value_at_sale", nullable = false, precision = 19, scale = 4)
    private BigDecimal netBookValueAtSale;

    // Computed: Gain/Loss = Sale Price - Net Book Value
    @Transient
    public BigDecimal getGainLossOnSale() {
        return salePrice.subtract(netBookValueAtSale);
    }

    // Sales Invoice
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_invoice_id")
    private Invoice salesInvoice;

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
