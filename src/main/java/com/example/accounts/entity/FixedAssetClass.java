package com.example.accounts.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing a Fixed Asset Class/Category
 * Examples: Buildings, Machinery, Vehicles, Computer Equipment, etc.
 */
@Entity
@Table(name = "fixed_asset_classes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixedAssetClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_id")
    private Long classId;

    @Column(name = "code", unique = true, length = 50)
    private String code;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "asset_type", nullable = false, length = 100)
    private String assetType = "Property, Plant and Equipment";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_class_id")
    private FixedAssetClass parentClass;

    @Column(name = "is_active")
    private Boolean isActive = true;

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
