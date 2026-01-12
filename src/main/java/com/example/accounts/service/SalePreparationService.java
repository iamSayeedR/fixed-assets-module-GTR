package com.example.accounts.service;

import com.example.accounts.entity.FixedAsset;
import com.example.accounts.entity.FixedAssetSalePreparation;
import com.example.accounts.entity.enums.AssetStatus;
import com.example.accounts.exception.BusinessException;
import com.example.accounts.exception.ResourceNotFoundException;
import com.example.accounts.repository.FixedAssetRepository;
import com.example.accounts.repository.FixedAssetSalePreparationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for Fixed Asset Sale Preparation (Held for Sale)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SalePreparationService {

    private final FixedAssetSalePreparationRepository salePreparationRepository;
    private final FixedAssetRepository fixedAssetRepository;

    /**
     * Create sale preparation
     */
    public FixedAssetSalePreparation createSalePreparation(FixedAssetSalePreparation preparation) {
        log.info("Creating sale preparation for asset: {}", preparation.getFixedAsset().getFixedAssetId());

        FixedAsset asset = preparation.getFixedAsset();

        // Validate asset can be prepared for sale
        if (asset.getStatus() != AssetStatus.ACTIVE && asset.getStatus() != AssetStatus.FULLY_DEPRECIATED) {
            throw new BusinessException(
                    "Asset must be ACTIVE or FULLY_DEPRECIATED to prepare for sale. Current status: "
                            + asset.getStatus());
        }

        // Record NBV at reclassification
        preparation.setNetBookValueAtReclassification(asset.getNetBookValue());

        FixedAssetSalePreparation saved = salePreparationRepository.save(preparation);

        log.info("Created sale preparation: {}", saved.getPreparationId());

        return saved;
    }

    /**
     * Post sale preparation (change status to HELD_FOR_SALE)
     */
    public void postSalePreparation(Long preparationId) {
        log.info("Posting sale preparation: {}", preparationId);

        FixedAssetSalePreparation preparation = salePreparationRepository.findById(preparationId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Sale preparation not found with id: " + preparationId));

        if (preparation.getIsPosted()) {
            throw new BusinessException("Sale preparation is already posted");
        }

        FixedAsset asset = preparation.getFixedAsset();

        // Change status to HELD_FOR_SALE
        asset.setStatus(AssetStatus.HELD_FOR_SALE);

        fixedAssetRepository.save(asset);

        // Mark preparation as posted
        preparation.setIsPosted(true);
        preparation.setPostedDate(LocalDateTime.now());

        salePreparationRepository.save(preparation);

        log.info("Posted sale preparation {} for asset {}. Asset is now HELD_FOR_SALE", preparationId,
                asset.getFixedAssetId());
    }

    /**
     * Cancel sale preparation (revert to ACTIVE)
     */
    public void cancelSalePreparation(Long preparationId) {
        log.info("Cancelling sale preparation: {}", preparationId);

        FixedAssetSalePreparation preparation = salePreparationRepository.findById(preparationId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Sale preparation not found with id: " + preparationId));

        if (!preparation.getIsPosted()) {
            throw new BusinessException("Sale preparation is not posted, cannot cancel");
        }

        if (preparation.getActualSale() != null) {
            throw new BusinessException("Cannot cancel sale preparation - asset has been sold");
        }

        FixedAsset asset = preparation.getFixedAsset();

        if (asset.getStatus() != AssetStatus.HELD_FOR_SALE) {
            throw new BusinessException("Asset is not HELD_FOR_SALE, cannot cancel preparation");
        }

        // Revert to ACTIVE
        asset.setStatus(AssetStatus.ACTIVE);

        fixedAssetRepository.save(asset);

        log.info("Cancelled sale preparation {}. Asset {} reverted to ACTIVE", preparationId, asset.getFixedAssetId());
    }

    /**
     * Get sale preparation by ID
     */
    @Transactional(readOnly = true)
    public FixedAssetSalePreparation getSalePreparationById(Long preparationId) {
        return salePreparationRepository.findById(preparationId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Sale preparation not found with id: " + preparationId));
    }

    /**
     * Get sale preparations by asset
     */
    @Transactional(readOnly = true)
    public List<FixedAssetSalePreparation> getSalePreparationsByAsset(Long assetId) {
        return salePreparationRepository.findByFixedAssetId(assetId);
    }

    /**
     * Get pending sales (preparations without actual sale)
     */
    @Transactional(readOnly = true)
    public List<FixedAssetSalePreparation> getPendingSales() {
        return salePreparationRepository.findPendingSales();
    }
}
