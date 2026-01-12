package com.example.accounts.dto;

import com.example.accounts.entity.enums.AccountBalanceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChartOfAccountRequest {

    @NotBlank(message = "Account code is required")
    private String accountCode;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Account type is required")
    private AccountBalanceType accountType;

    @NotNull(message = "Section is required")
    private String section;

    private String parentGroup;

    @Builder.Default
    private Boolean isOffBalance = false;

    @Builder.Default
    private Boolean isQuantitative = false;

    private String currency;

    private String details;

    @Builder.Default
    private List<AccountDimensionRequest> dimensions = new ArrayList<>();
}
