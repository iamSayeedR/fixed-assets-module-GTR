package com.example.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating/updating Fixed Asset Class
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixedAssetClassRequest {

    private String code;
    private String description;
    private String assetType;
    private Long parentClassId;
    private Boolean isActive;
}
