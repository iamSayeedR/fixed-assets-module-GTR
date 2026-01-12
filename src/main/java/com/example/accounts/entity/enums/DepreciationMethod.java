package com.example.accounts.entity.enums;

/**
 * Depreciation calculation method for Fixed Assets
 */
public enum DepreciationMethod {
    /**
     * Straight Line Depreciation
     * Formula: (Cost - Salvage Value) / Useful Life in Months
     */
    STRAIGHT_LINE,

    /**
     * Units of Production Depreciation
     * Formula: (Cost - Salvage Value) / Total Units * Units Used
     */
    UNITS_OF_PRODUCTION
}
