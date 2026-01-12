package com.example.accounts.entity.enums;

/**
 * Type of parameter change/reassessment for Fixed Assets
 */
public enum ParameterChangeType {
    /**
     * Asset value decreased due to impairment
     */
    IMPAIRMENT,

    /**
     * Asset value increased due to revaluation
     */
    REVALUATION,

    /**
     * Useful life extended or reduced
     */
    USEFUL_LIFE_CHANGE,

    /**
     * Salvage value changed
     */
    SALVAGE_VALUE_CHANGE
}
