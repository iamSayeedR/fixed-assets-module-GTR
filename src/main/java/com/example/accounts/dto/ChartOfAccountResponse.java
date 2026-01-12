package com.example.accounts.dto;

import com.example.accounts.entity.enums.AccountBalanceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChartOfAccountResponse {
    private Long accountId;
    private String accountCode;
    private String description;
    private AccountBalanceType accountType;
    private String section;
    private String parentGroup;
    private Boolean isOffBalance;
    private Boolean isQuantitative;
    private String currency;
    private Boolean isActive;
    private String details;

    @Builder.Default
    private List<AccountDimensionResponse> dimensions = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
