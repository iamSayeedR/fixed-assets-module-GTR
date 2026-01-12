package com.example.accounts.entity.enums;

/**
 * Status of a Fixed Asset throughout its lifecycle
 */
public enum AssetStatus {
    /**
     * Asset created but not yet activated
     */
    NEW,

    /**
     * Asset is active and being depreciated
     */
    ACTIVE,

    /**
     * Asset is under construction
     */
    CONSTRUCTION_IN_PROGRESS,

    /**
     * Construction completed, ready to activate
     */
    CONSTRUCTION_COMPLETED,

    /**
     * Asset fully depreciated (NBV = Salvage Value)
     */
    FULLY_DEPRECIATED,

    /**
     * Asset placed in conservation (depreciation paused)
     */
    IN_CONSERVATION,

    /**
     * Asset reclassified as held for sale
     */
    HELD_FOR_SALE,

    /**
     * Asset disposed/sold
     */
    DISPOSED,

    /**
     * Asset written off
     */
    WRITTEN_OFF
}
