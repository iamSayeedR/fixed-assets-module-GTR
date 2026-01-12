package com.example.accounts.entity.enums;

/**
 * Type of asset disposal
 */
public enum DisposalType {
    /**
     * Asset sold to a buyer
     */
    SALE,

    /**
     * Asset written off due to damage/obsolescence
     */
    WRITE_OFF,

    /**
     * Asset donated
     */
    DONATION,

    /**
     * Asset traded in for a new asset
     */
    TRADE_IN
}
