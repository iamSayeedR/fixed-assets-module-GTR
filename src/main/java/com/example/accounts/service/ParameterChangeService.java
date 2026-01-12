package com.example.accounts.service;

import com.example.accounts.entity.FixedAsset;
import com.example.accounts.entity.FixedAssetParameterChange;
import com.example.accounts.entity.enums.AssetStatus;
import com.example.accounts.exception.BusinessException;
import com.example.accounts.exception.ResourceNotFoundException;
import com.example.accounts.repository.FixedAssetParameterChangeRepository;
import com.example.accounts.repository.FixedAssetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for Fixed Asset Parameter Changes (Reassessments)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ParameterChangeService {

    private final FixedAssetParameterChangeRepository parameterChangeRepository;
    private final FixedAssetRepository fixedAssetRepository;

    /**
     * Create parameter change
     */
    public FixedAssetParameterChange createParameterChange(FixedAssetParameterChange change) {
        log.info("Creating parameter change for asset: {}", change.getFixedAsset().getFixedAssetId());

        FixedAsset asset = change.getFixedAsset();

        // Validate asset is active or fully depreciated
        if (asset.getStatus() != AssetStatus.ACTIVE && asset.getStatus() != AssetStatus.FULLY_DEPRECIATED) {
            throw new BusinessException(
                    "Asset must be ACTIVE or FULLY_DEPRECIATED for parameter changes. Current status: "
                            + asset.getStatus());
        }

        // Record old values
        change.setOldGrossCost(asset.getGrossCost());
        change.setOldSalvageValue(asset.getSalvageValue());
        change.setOldUsefulLifeMonths(asset.getUsefulLifeMonths());
        change.setOldAccumulatedDepreciation(asset.getAccumulatedDepreciation());

        FixedAssetParameterChange saved = parameterChangeRepository.save(change);

        log.info("Created parameter change: {}", saved.getChangeId());

        return saved;
    }

    /**
     * Post parameter change (apply changes to asset)
     */
    public void postParameterChange(Long changeId) {
        log.info("Posting parameter change: {}", changeId);

        FixedAssetParameterChange change = parameterChangeRepository.findById(changeId)
                .orElseThrow(() -> new ResourceNotFoundException("Parameter change not found with id: " + changeId));

        if (change.getIsPosted()) {
            throw new BusinessException("Parameter change is already posted");
        }

        FixedAsset asset = change.getFixedAsset();

        // Apply changes based on type
        switch (change.getChangeType()) {
            case IMPAIRMENT:
                applyImpairment(asset, change);
                break;
            case REVALUATION:
                applyRevaluation(asset, change);
                break;
            case USEFUL_LIFE_CHANGE:
                applyUsefulLifeChange(asset, change);
                break;
            case SALVAGE_VALUE_CHANGE:
                applySalvageValueChange(asset, change);
                break;
        }

        fixedAssetRepository.save(asset);

        // Mark change as posted
        change.setIsPosted(true);
        change.setPostedDate(LocalDateTime.now());

        parameterChangeRepository.save(change);

        log.info("Posted parameter change {} for asset {}", changeId, asset.getFixedAssetId());
    }

    /**
     * Apply impairment (reduce asset value)
     */
    private void applyImpairment(FixedAsset asset, FixedAssetParameterChange change) {
        if (change.getAdjustmentAmount() == null || change.getAdjustmentAmount().signum() >= 0) {
            throw new BusinessException("Impairment adjustment must be negative");
        }

        // Reduce cost adjustment
        BigDecimal currentAdjustment = asset.getCostAdjustment() != null ? asset.getCostAdjustment() : BigDecimal.ZERO;
        asset.setCostAdjustment(currentAdjustment.add(change.getAdjustmentAmount()));

        log.info("Applied impairment of {}", change.getAdjustmentAmount());
    }

    /**
     * Apply revaluation (increase asset value)
     */
    private void applyRevaluation(FixedAsset asset, FixedAssetParameterChange change) {
        if (change.getAdjustmentAmount() == null || change.getAdjustmentAmount().signum() <= 0) {
            throw new BusinessException("Revaluation adjustment must be positive");
        }

        // Increase cost adjustment
        BigDecimal currentAdjustment = asset.getCostAdjustment() != null ? asset.getCostAdjustment() : BigDecimal.ZERO;
        asset.setCostAdjustment(currentAdjustment.add(change.getAdjustmentAmount()));

        log.info("Applied revaluation of {}", change.getAdjustmentAmount());
    }

    /**
     * Apply useful life change
     */
    private void applyUsefulLifeChange(FixedAsset asset, FixedAssetParameterChange change) {
        if (change.getNewUsefulLifeMonths() == null) {
            throw new BusinessException("New useful life is required");
        }

        asset.setUsefulLifeMonths(change.getNewUsefulLifeMonths());

        log.info("Changed useful life from {} to {} months", change.getOldUsefulLifeMonths(),
                change.getNewUsefulLifeMonths());
    }

    /**
     * Apply salvage value change
     */
    private void applySalvageValueChange(FixedAsset asset, FixedAssetParameterChange change) {
        if (change.getNewSalvageValue() == null) {
            throw new BusinessException("New salvage value is required");
        }

        asset.setSalvageValue(change.getNewSalvageValue());

        log.info("Changed salvage value from {} to {}", change.getOldSalvageValue(), change.getNewSalvageValue());
    }

    /**
     * Get parameter change by ID
     */
    @Transactional(readOnly = true)
    public FixedAssetParameterChange getParameterChangeById(Long changeId) {
        return parameterChangeRepository.findById(changeId)
                .orElseThrow(() -> new ResourceNotFoundException("Parameter change not found with id: " + changeId));
    }

    /**
     * Get parameter changes by asset
     */
    @Transactional(readOnly = true)
    public List<FixedAssetParameterChange> getParameterChangesByAsset(Long assetId) {
        return parameterChangeRepository.findByFixedAssetId(assetId);
    }

    /**
     * Get unposted parameter changes
     */
    @Transactional(readOnly = true)
    public List<FixedAssetParameterChange> getUnpostedParameterChanges() {
        return parameterChangeRepository.findByIsPostedFalse();
    }
}
