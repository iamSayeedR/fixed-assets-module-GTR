package com.example.accounts.entity;

import com.example.accounts.entity.enums.AccountBalanceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chart_of_accounts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChartOfAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "account_code", unique = true, nullable = false, length = 20)
    private String accountCode;

    @Column(nullable = false, length = 200)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountBalanceType accountType;

    @Column(nullable = false, length = 200)
    private String section;

    @Column(name = "parent_group", length = 200)
    private String parentGroup;

    @Column(name = "is_off_balance", nullable = false)
    @Builder.Default
    private Boolean isOffBalance = false;

    @Column(name = "is_quantitative", nullable = false)
    @Builder.Default
    private Boolean isQuantitative = false;

    @Column(name = "currency", length = 3)
    @Builder.Default
    private String currency = "USD";

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(columnDefinition = "TEXT")
    private String details;

    @OneToMany(mappedBy = "chartOfAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AccountDimension> dimensions = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Helper method to add dimension
    public void addDimension(AccountDimension dimension) {
        dimensions.add(dimension);
        dimension.setChartOfAccount(this);
    }

    // Helper method to remove dimension
    public void removeDimension(AccountDimension dimension) {
        dimensions.remove(dimension);
        dimension.setChartOfAccount(null);
    }
}
