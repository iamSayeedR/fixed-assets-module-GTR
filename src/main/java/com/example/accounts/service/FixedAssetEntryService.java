package com.example.accounts.service;

import com.example.accounts.entity.*;
import com.example.accounts.entity.enums.AssetStatus;
import com.example.accounts.exception.BusinessException;
import com.example.accounts.exception.ResourceNotFoundException;
import com.example.accounts.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

/**
 * Service for Fixed Asset Entry operations (Asset Activation)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FixedAssetEntryService {

    private final FixedAssetEntryRepository entryRepository;
    private final FixedAssetRepository fixedAssetRepository;
    private final ChartOfAccountRepository chartOfAccountRepository;

    /**
     * Create and post a fixed asset entry (activates the asset)
     */
    public FixedAssetEntry createAndPostEntry(FixedAssetEntry entry) {
        log.info("Creating fixed asset entry for asset: {}", entry.getFixedAsset().getFixedAssetId());

        FixedAsset asset = entry.getFixedAsset();

        // Validate asset can be activated
        if (asset.getStatus() != AssetStatus.NEW && asset.getStatus() != AssetStatus.CONSTRUCTION_COMPLETED) {
            throw new BusinessException("Asset must be NEW or CONSTRUCTION_COMPLETED to create entry. Current status: "
                    + asset.getStatus());
        }

        // Validate entry data
        validateEntry(entry);

        // Save entry
        FixedAssetEntry saved = entryRepository.save(entry);

        // Post the entry (activate asset)
        postEntry(saved.getEntryId());

        return saved;
    }

    /**
     * Post entry (activate asset and set depreciation parameters)
     */
    public void postEntry(Long entryId) {
        log.info("Posting fixed asset entry: {}", entryId);

        FixedAssetEntry entry = entryRepository.findById(entryId)
                .orElseThrow(() -> new ResourceNotFoundException("Fixed asset entry not found with id: " + entryId));

        if (entry.getIsPosted()) {
            throw new BusinessException("Entry is already posted");
        }

        FixedAsset asset = entry.getFixedAsset();

        // Update asset with entry values
        asset.setInitialCost(entry.getInitialCost());
        asset.setSalvageValue(entry.getSalvageValue());
        asset.setDepreciationMethod(entry.getDepreciationMethod());
        asset.setUsefulLifeMonths(entry.getUsefulLifeMonths());
        asset.setTotalUnits(entry.getTotalUnits());

        if (entry.getTotalUnits() != null) {
            asset.setRemainingUnits(entry.getTotalUnits());
        }

        // Set depreciation dates
        asset.setDepreciationStartDate(entry.getDepreciationStartDate());
        asset.setNextDepreciationDate(
                entry.getDepreciationStartDate()
                        .plusMonths(1)
                        .with(TemporalAdjusters.lastDayOfMonth()));

        // Override GL accounts if provided in entry
        if (entry.getGlAccount() != null) {
            asset.setGlAccount(entry.getGlAccount());
        }
        if (entry.getDepreciationGlAccount() != null) {
            asset.setDepreciationGlAccount(entry.getDepreciationGlAccount());
        }
        if (entry.getExpenseGlAccount() != null) {
            asset.setExpenseGlAccount(entry.getExpenseGlAccount());
        }

        // Change status to ACTIVE
        asset.setStatus(AssetStatus.ACTIVE);
        asset.setActivationDate(entry.getEntryDate());

        fixedAssetRepository.save(asset);

        // Mark entry as posted
        entry.setIsPosted(true);
        entry.setPostedDate(LocalDateTime.now());

        entryRepository.save(entry);

        log.info("Posted fixed asset entry {} and activated asset {}", entryId, asset.getFixedAssetId());
    }

    /**
     * Get entry by ID
     */
    @Transactional(readOnly = true)
    public FixedAssetEntry getEntryById(Long entryId) {
        return entryRepository.findById(entryId)
                .orElseThrow(() -> new ResourceNotFoundException("Fixed asset entry not found with id: " + entryId));
    }

    /**
     * Get entries by asset
     */
    @Transactional(readOnly = true)
    public java.util.List<FixedAssetEntry> getEntriesByAsset(Long assetId) {
        return entryRepository.findByFixedAssetId(assetId);
    }

    /**
     * Validate entry
     */
    private void validateEntry(FixedAssetEntry entry) {
        if (entry.getInitialCost() == null || entry.getInitialCost().signum() <= 0) {
            throw new BusinessException("Initial cost must be greater than zero");
        }

        if (entry.getDepreciationMethod() == null) {
            throw new BusinessException("Depreciation method is required");
        }

        if (entry.getDepreciationStartDate() == null) {
            throw new BusinessException("Depreciation start date is required");
        }

        switch (entry.getDepreciationMethod()) {
            case STRAIGHT_LINE:
                if (entry.getUsefulLifeMonths() == null || entry.getUsefulLifeMonths() <= 0) {
                    throw new BusinessException("Useful life in months is required for straight line depreciation");
                }
                break;
            case UNITS_OF_PRODUCTION:
                if (entry.getTotalUnits() == null || entry.getTotalUnits() <= 0) {
                    throw new BusinessException("Total units is required for units of production depreciation");
                }
                break;
        }
    }
}
