package com.example.accounts.service;

import com.example.accounts.entity.FixedAsset;
import com.example.accounts.entity.FixedAssetCapitalImprovement;
import com.example.accounts.entity.enums.AssetStatus;
import com.example.accounts.exception.BusinessException;
import com.example.accounts.exception.ResourceNotFoundException;
import com.example.accounts.repository.FixedAssetCapitalImprovementRepository;
import com.example.accounts.repository.FixedAssetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for Fixed Asset Capital Improvements
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CapitalImprovementService {

    private final FixedAssetCapitalImprovementRepository improvementRepository;
    private final FixedAssetRepository fixedAssetRepository;

    /**
     * Create capital improvement
     */
    public FixedAssetCapitalImprovement createImprovement(FixedAssetCapitalImprovement improvement) {
        log.info("Creating capital improvement for asset: {}", improvement.getFixedAsset().getFixedAssetId());

        FixedAsset asset = improvement.getFixedAsset();

        // Validate asset is active
        if (asset.getStatus() != AssetStatus.ACTIVE) {
            throw new BusinessException(
                    "Asset must be ACTIVE for capital improvements. Current status: " + asset.getStatus());
        }

        // Validate improvement cost
        if (improvement.getImprovementCost() == null || improvement.getImprovementCost().signum() <= 0) {
            throw new BusinessException("Improvement cost must be greater than zero");
        }

        FixedAssetCapitalImprovement saved = improvementRepository.save(improvement);

        log.info("Created capital improvement: {}", saved.getImprovementId());

        return saved;
    }

    /**
     * Post improvement (update asset cost and useful life)
     */
    public void postImprovement(Long improvementId) {
        log.info("Posting capital improvement: {}", improvementId);

        FixedAssetCapitalImprovement improvement = improvementRepository.findById(improvementId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Capital improvement not found with id: " + improvementId));

        if (improvement.getIsPosted()) {
            throw new BusinessException("Improvement is already posted");
        }

        FixedAsset asset = improvement.getFixedAsset();

        // Update asset cost
        BigDecimal currentAdjustment = asset.getCostAdjustment() != null ? asset.getCostAdjustment() : BigDecimal.ZERO;
        asset.setCostAdjustment(currentAdjustment.add(improvement.getImprovementCost()));

        // Extend useful life if applicable
        if (improvement.getExtendsUsefulLifeMonths() != null && improvement.getExtendsUsefulLifeMonths() > 0) {
            if (asset.getUsefulLifeMonths() != null) {
                asset.setUsefulLifeMonths(asset.getUsefulLifeMonths() + improvement.getExtendsUsefulLifeMonths());
                log.info("Extended useful life by {} months", improvement.getExtendsUsefulLifeMonths());
            }
        }

        // Increase salvage value if applicable
        if (improvement.getIncreasesSalvageValue() != null && improvement.getIncreasesSalvageValue().signum() > 0) {
            BigDecimal currentSalvage = asset.getSalvageValue() != null ? asset.getSalvageValue() : BigDecimal.ZERO;
            asset.setSalvageValue(currentSalvage.add(improvement.getIncreasesSalvageValue()));
            log.info("Increased salvage value by {}", improvement.getIncreasesSalvageValue());
        }

        fixedAssetRepository.save(asset);

        // Mark improvement as posted
        improvement.setIsPosted(true);
        improvement.setPostedDate(LocalDateTime.now());

        improvementRepository.save(improvement);

        log.info("Posted capital improvement {} for asset {}", improvementId, asset.getFixedAssetId());
    }

    /**
     * Get improvement by ID
     */
    @Transactional(readOnly = true)
    public FixedAssetCapitalImprovement getImprovementById(Long improvementId) {
        return improvementRepository.findById(improvementId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Capital improvement not found with id: " + improvementId));
    }

    /**
     * Get improvements by asset
     */
    @Transactional(readOnly = true)
    public List<FixedAssetCapitalImprovement> getImprovementsByAsset(Long assetId) {
        return improvementRepository.findByFixedAssetId(assetId);
    }

    /**
     * Get unposted improvements
     */
    @Transactional(readOnly = true)
    public List<FixedAssetCapitalImprovement> getUnpostedImprovements() {
        return improvementRepository.findByIsPostedFalse();
    }
}
