package com.example.accounts.service;

import com.example.accounts.entity.*;
import com.example.accounts.entity.enums.AssetStatus;
import com.example.accounts.entity.enums.DepreciationMethod;
import com.example.accounts.exception.BusinessException;
import com.example.accounts.exception.ResourceNotFoundException;
import com.example.accounts.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for Fixed Asset Depreciation calculations and processing
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DepreciationService {

    private final FixedAssetRepository fixedAssetRepository;
    private final FixedAssetDepreciationRepository depreciationRepository;
    private final FixedAssetMonthlyUsageRepository monthlyUsageRepository;
    private final JournalEntryService journalEntryService;

    /**
     * Calculate and create depreciation for a specific asset and period
     */
    public FixedAssetDepreciation calculateDepreciation(Long assetId, LocalDate period) {
        log.info("Calculating depreciation for asset {} for period {}", assetId, period);

        FixedAsset asset = findAssetById(assetId);

        // Validate asset can be depreciated
        validateAssetForDepreciation(asset, period);

        // Check if depreciation already exists for this period
        if (depreciationRepository.findByFixedAssetIdAndPeriod(assetId, period).isPresent()) {
            throw new BusinessException("Depreciation already exists for asset " + assetId + " for period " + period);
        }

        // Calculate depreciation amount based on method
        BigDecimal depreciationAmount = calculateDepreciationAmount(asset, period);

        // Create depreciation record
        FixedAssetDepreciation depreciation = new FixedAssetDepreciation();
        depreciation.setFixedAsset(asset);
        depreciation.setDepreciationPeriod(period);
        depreciation.setDepreciationDate(LocalDate.now());

        // Set opening balances
        depreciation.setOpeningGrossCost(asset.getGrossCost());
        depreciation.setOpeningAccumulatedDepreciation(asset.getAccumulatedDepreciation());
        depreciation.setOpeningNetBookValue(asset.getNetBookValue());

        // Set depreciation amount
        depreciation.setDepreciationAmount(depreciationAmount);

        // Calculate closing balances
        BigDecimal closingAccumulatedDepreciation = asset.getAccumulatedDepreciation().add(depreciationAmount);
        BigDecimal closingNetBookValue = asset.getGrossCost().subtract(closingAccumulatedDepreciation);

        depreciation.setClosingAccumulatedDepreciation(closingAccumulatedDepreciation);
        depreciation.setClosingNetBookValue(closingNetBookValue);

        // Set description
        depreciation.setDescription("Depreciation for " + period.getMonth() + " " + period.getYear());

        // Save depreciation
        FixedAssetDepreciation saved = depreciationRepository.save(depreciation);

        // Update asset
        asset.setAccumulatedDepreciation(closingAccumulatedDepreciation);
        asset.setLastDepreciationDate(period);
        asset.setLastDepreciationCalculationDate(LocalDateTime.now());
        asset.setNextDepreciationDate(period.plusMonths(1).with(TemporalAdjusters.lastDayOfMonth()));

        // Check if fully depreciated
        if (closingNetBookValue.compareTo(asset.getSalvageValue()) <= 0) {
            asset.setStatus(AssetStatus.FULLY_DEPRECIATED);
            log.info("Asset {} is now fully depreciated", assetId);
        }

        fixedAssetRepository.save(asset);

        log.info("Created depreciation {} for asset {} with amount {}", saved.getDepreciationId(), assetId,
                depreciationAmount);

        return saved;
    }

    /**
     * Calculate depreciation amount based on method
     */
    private BigDecimal calculateDepreciationAmount(FixedAsset asset, LocalDate period) {
        return switch (asset.getDepreciationMethod()) {
            case STRAIGHT_LINE -> calculateStraightLineDepreciation(asset);
            case UNITS_OF_PRODUCTION -> calculateUnitsOfProductionDepreciation(asset, period);
        };
    }

    /**
     * Calculate straight line depreciation
     * Formula: (Gross Cost - Salvage Value) / Useful Life in Months
     */
    private BigDecimal calculateStraightLineDepreciation(FixedAsset asset) {
        if (asset.getUsefulLifeMonths() == null || asset.getUsefulLifeMonths() == 0) {
            throw new BusinessException("Useful life in months is required for straight line depreciation");
        }

        BigDecimal depreciableAmount = asset.getGrossCost().subtract(asset.getSalvageValue());
        BigDecimal monthlyDepreciation = depreciableAmount.divide(
                BigDecimal.valueOf(asset.getUsefulLifeMonths()),
                4,
                RoundingMode.HALF_UP);

        // Ensure depreciation doesn't bring NBV below salvage value
        BigDecimal maxDepreciation = asset.getNetBookValue().subtract(asset.getSalvageValue());
        if (monthlyDepreciation.compareTo(maxDepreciation) > 0) {
            monthlyDepreciation = maxDepreciation;
        }

        return monthlyDepreciation;
    }

    /**
     * Calculate units of production depreciation
     * Formula: (Gross Cost - Salvage Value) / Total Units × Units Used
     */
    private BigDecimal calculateUnitsOfProductionDepreciation(FixedAsset asset, LocalDate period) {
        if (asset.getTotalUnits() == null || asset.getTotalUnits() == 0) {
            throw new BusinessException("Total units is required for units of production depreciation");
        }

        // Get monthly usage for this period
        FixedAssetMonthlyUsage usage = monthlyUsageRepository
                .findByFixedAssetIdAndPeriod(asset.getFixedAssetId(), period)
                .orElseThrow(() -> new BusinessException(
                        "Monthly usage not found for asset " + asset.getFixedAssetId() + " for period " + period));

        if (!usage.getIsProcessed()) {
            throw new BusinessException("Monthly usage for period " + period + " has not been processed yet");
        }

        BigDecimal depreciableAmount = asset.getGrossCost().subtract(asset.getSalvageValue());
        BigDecimal depreciationPerUnit = depreciableAmount.divide(
                BigDecimal.valueOf(asset.getTotalUnits()),
                4,
                RoundingMode.HALF_UP);

        BigDecimal depreciation = depreciationPerUnit.multiply(BigDecimal.valueOf(usage.getUnitsUsed()));

        // Ensure depreciation doesn't bring NBV below salvage value
        BigDecimal maxDepreciation = asset.getNetBookValue().subtract(asset.getSalvageValue());
        if (depreciation.compareTo(maxDepreciation) > 0) {
            depreciation = maxDepreciation;
        }

        return depreciation;
    }

    /**
     * Calculate depreciation for all active assets for a given period
     */
    public List<FixedAssetDepreciation> calculateMonthlyDepreciation(LocalDate period) {
        log.info("Calculating depreciation for all assets for period {}", period);

        List<FixedAsset> assets = fixedAssetRepository.findAssetsNeedingDepreciation(period);
        List<FixedAssetDepreciation> depreciations = new ArrayList<>();

        int successCount = 0;
        int errorCount = 0;

        for (FixedAsset asset : assets) {
            try {
                FixedAssetDepreciation depreciation = calculateDepreciation(asset.getFixedAssetId(), period);
                depreciations.add(depreciation);
                successCount++;
            } catch (Exception e) {
                log.error("Error calculating depreciation for asset {}: {}", asset.getFixedAssetId(), e.getMessage());
                errorCount++;
            }
        }

        log.info("Completed depreciation calculation for period {}. Success: {}, Errors: {}", period, successCount,
                errorCount);

        return depreciations;
    }

    /**
     * Post depreciation to GL (create journal entry)
     */
    public void postDepreciation(Long depreciationId) {
        log.info("Posting depreciation: {}", depreciationId);

        FixedAssetDepreciation depreciation = depreciationRepository.findById(depreciationId)
                .orElseThrow(() -> new ResourceNotFoundException("Depreciation not found with id: " + depreciationId));

        if (depreciation.getIsPosted()) {
            throw new BusinessException("Depreciation " + depreciationId + " is already posted");
        }

        FixedAsset asset = depreciation.getFixedAsset();

        // Create journal entry
        // Dr. Depreciation Expense
        // Cr. Accumulated Depreciation

        // TODO: Implement journal entry creation via JournalEntryService
        // For now, just mark as posted

        depreciation.setIsPosted(true);
        depreciation.setPostedDate(LocalDateTime.now());

        depreciationRepository.save(depreciation);

        log.info("Posted depreciation: {}", depreciationId);
    }

    /**
     * Get depreciation history for an asset
     */
    @Transactional(readOnly = true)
    public List<FixedAssetDepreciation> getDepreciationHistory(Long assetId) {
        return depreciationRepository.findByFixedAssetId(assetId);
    }

    /**
     * Get depreciation for a specific period
     */
    @Transactional(readOnly = true)
    public List<FixedAssetDepreciation> getDepreciationByPeriod(LocalDate period) {
        return depreciationRepository.findByDepreciationPeriod(period);
    }

    /**
     * Validate asset can be depreciated
     */
    private void validateAssetForDepreciation(FixedAsset asset, LocalDate period) {
        if (asset.getStatus() != AssetStatus.ACTIVE) {
            throw new BusinessException("Asset must be ACTIVE to depreciate. Current status: " + asset.getStatus());
        }

        // Skip assets in conservation (depreciation is paused)
        if (asset.getStatus() == AssetStatus.IN_CONSERVATION) {
            throw new BusinessException("Asset is in conservation. Depreciation is paused.");
        }

        if (asset.getDepreciationStartDate() == null) {
            throw new BusinessException("Depreciation start date is not set for asset " + asset.getFixedAssetId());
        }

        if (period.isBefore(asset.getDepreciationStartDate())) {
            throw new BusinessException(
                    "Cannot depreciate before depreciation start date: " + asset.getDepreciationStartDate());
        }

        if (asset.getNetBookValue().compareTo(asset.getSalvageValue()) <= 0) {
            throw new BusinessException("Asset is already fully depreciated");
        }

        // Check if already depreciated for this period
        if (asset.getLastDepreciationDate() != null && !period.isAfter(asset.getLastDepreciationDate())) {
            throw new BusinessException("Asset has already been depreciated for period " + period);
        }
    }

    /**
     * Find asset by ID or throw exception
     */
    private FixedAsset findAssetById(Long assetId) {
        return fixedAssetRepository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Fixed asset not found with id: " + assetId));
    }
}
