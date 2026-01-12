package com.example.accounts.service;

import com.example.accounts.entity.FixedAsset;
import com.example.accounts.entity.FixedAssetMonthlyUsage;
import com.example.accounts.entity.enums.AssetStatus;
import com.example.accounts.entity.enums.DepreciationMethod;
import com.example.accounts.exception.BusinessException;
import com.example.accounts.exception.ResourceNotFoundException;
import com.example.accounts.repository.FixedAssetMonthlyUsageRepository;
import com.example.accounts.repository.FixedAssetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for Fixed Asset Monthly Usage operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MonthlyUsageService {

    private final FixedAssetMonthlyUsageRepository usageRepository;
    private final FixedAssetRepository fixedAssetRepository;

    /**
     * Record monthly usage for an asset
     */
    public FixedAssetMonthlyUsage recordUsage(Long assetId, LocalDate period, Integer unitsUsed, String notes) {
        log.info("Recording usage for asset {} for period {}: {} units", assetId, period, unitsUsed);

        FixedAsset asset = findAssetById(assetId);

        // Validate asset uses units of production
        if (asset.getDepreciationMethod() != DepreciationMethod.UNITS_OF_PRODUCTION) {
            throw new BusinessException("Asset does not use units of production depreciation method");
        }

        // Validate asset is active
        if (asset.getStatus() != AssetStatus.ACTIVE) {
            throw new BusinessException("Asset must be ACTIVE to record usage. Current status: " + asset.getStatus());
        }

        // Check if usage already exists for this period
        if (usageRepository.findByFixedAssetIdAndPeriod(assetId, period).isPresent()) {
            throw new BusinessException("Usage already recorded for asset " + assetId + " for period " + period);
        }

        // Validate units used
        if (unitsUsed == null || unitsUsed <= 0) {
            throw new BusinessException("Units used must be greater than zero");
        }

        // Check remaining units
        if (asset.getRemainingUnits() != null && unitsUsed > asset.getRemainingUnits()) {
            throw new BusinessException(
                    "Units used (" + unitsUsed + ") exceeds remaining units (" + asset.getRemainingUnits() + ")");
        }

        // Create usage record
        FixedAssetMonthlyUsage usage = new FixedAssetMonthlyUsage();
        usage.setFixedAsset(asset);
        usage.setUsagePeriod(period);
        usage.setUsageDate(LocalDate.now());
        usage.setUnitsUsed(unitsUsed);
        usage.setNotes(notes);

        FixedAssetMonthlyUsage saved = usageRepository.save(usage);

        log.info("Recorded usage {} for asset {}", saved.getUsageId(), assetId);

        return saved;
    }

    /**
     * Process usage (mark as ready for depreciation)
     */
    public void processUsage(Long usageId) {
        log.info("Processing usage: {}", usageId);

        FixedAssetMonthlyUsage usage = usageRepository.findById(usageId)
                .orElseThrow(() -> new ResourceNotFoundException("Usage not found with id: " + usageId));

        if (usage.getIsProcessed()) {
            throw new BusinessException("Usage is already processed");
        }

        // Update remaining units
        FixedAsset asset = usage.getFixedAsset();
        if (asset.getRemainingUnits() != null) {
            asset.setRemainingUnits(asset.getRemainingUnits() - usage.getUnitsUsed());
            fixedAssetRepository.save(asset);
        }

        // Mark as processed
        usage.setIsProcessed(true);
        usage.setProcessedDate(LocalDateTime.now());

        usageRepository.save(usage);

        log.info("Processed usage: {}", usageId);
    }

    /**
     * Get usage by ID
     */
    @Transactional(readOnly = true)
    public FixedAssetMonthlyUsage getUsageById(Long usageId) {
        return usageRepository.findById(usageId)
                .orElseThrow(() -> new ResourceNotFoundException("Usage not found with id: " + usageId));
    }

    /**
     * Get usage history for an asset
     */
    @Transactional(readOnly = true)
    public List<FixedAssetMonthlyUsage> getUsageByAsset(Long assetId) {
        return usageRepository.findByFixedAssetId(assetId);
    }

    /**
     * Get unprocessed usage
     */
    @Transactional(readOnly = true)
    public List<FixedAssetMonthlyUsage> getUnprocessedUsage() {
        return usageRepository.findByIsProcessedFalse();
    }

    /**
     * Find asset by ID or throw exception
     */
    private FixedAsset findAssetById(Long assetId) {
        return fixedAssetRepository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Fixed asset not found with id: " + assetId));
    }
}
