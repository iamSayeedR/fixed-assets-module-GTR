package com.example.accounts.dto;

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
public class ChartOfAccountTreeNode {
    private Long accountId;
    private String accountCode;
    private String description;
    private String accountType;
    private String section;
    private String parentGroup;
    private String currency;
    private Boolean isActive;
    private Boolean isOffBalance;
    private Boolean isQuantitative;

    // Tree structure
    private Integer level;
    private Boolean hasChildren;

    @Builder.Default
    private List<ChartOfAccountTreeNode> children = new ArrayList<>();

    // Dimensions (simplified - just show if dimension exists)
    private Boolean hasDimension1;
    private Boolean hasDimension2;
    private Boolean hasDimension3;
}
