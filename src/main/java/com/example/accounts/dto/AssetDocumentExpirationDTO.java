package com.example.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for Fixed Asset Document Expiration Tracking
 * Tracks insurance, certificates, maintenance schedules, etc.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetDocumentExpirationDTO {

    private Long fixedAssetId;
    private String assetNumber;
    private String assetDescription;
    private String documentType; // Insurance, Certificate, Maintenance, etc.
    private LocalDate expirationDate;
    private Integer daysBeforeExpiration;
    private String status; // Active, Expiring Soon, Expired
    private String notes;

    // Computed field
    public String getExpirationStatus() {
        if (daysBeforeExpiration == null) {
            return "Unknown";
        }
        if (daysBeforeExpiration < 0) {
            return "Expired";
        } else if (daysBeforeExpiration <= 30) {
            return "Expiring Soon";
        } else {
            return "Active";
        }
    }
}
