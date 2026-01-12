package com.example.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for Fixed Asset Class
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixedAssetClassResponse {

    private Long classId;
    private String code;
    private String description;
    private String assetType;
    private Long parentClassId;
    private String parentClassName;
    private Boolean isActive;
}
