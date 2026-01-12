package com.example.accounts.service;

import com.example.accounts.entity.FixedAsset;
import com.example.accounts.entity.FixedAssetConservation;
import com.example.accounts.entity.enums.AssetStatus;
import com.example.accounts.exception.BusinessException;
import com.example.accounts.exception.ResourceNotFoundException;
import com.example.accounts.repository.FixedAssetConservationRepository;
import com.example.accounts.repository.FixedAssetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for Fixed Asset Conservation operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ConservationService {

    private final FixedAssetConservationRepository conservationRepository;
    private final FixedAssetRepository fixedAssetRepository;

    /**
     * Start conservation for an asset
     */
    public FixedAssetConservation startConservation(FixedAssetConservation conservation) {
        log.info("Starting conservation for asset: {}", conservation.getFixedAsset().getFixedAssetId());

        FixedAsset asset = conservation.getFixedAsset();

        // Validate asset status
        if (asset.getStatus() != AssetStatus.ACTIVE) {
            throw new BusinessException(
                    "Only ACTIVE assets can be placed in conservation. Current status: " + asset.getStatus());
        }

        // Check if asset already in conservation
        Optional<FixedAssetConservation> existingConservation = conservationRepository
                .findActiveConservationByAssetId(asset.getFixedAssetId());
        if (existingConservation.isPresent()) {
            throw new BusinessException("Asset is already in conservation");
        }

        // Set transaction type
        conservation.setTransactionType("Start of Conservation");

        // Capture financial snapshot
        conservation.setGrossCostAtConservation(asset.getGrossCost());
        conservation.setSalvageValueAtConservation(asset.getSalvageValue());
        conservation.setAccumulatedDepreciationAtConservation(asset.getAccumulatedDepreciation());
        conservation.setNetBookValueAtConservation(asset.getNetBookValue());
        conservation.setUsefulLifeMonthsAtConservation(asset.getUsefulLifeMonths());
        conservation.setDepreciationMethodAtConservation(
                asset.getDepreciationMethod() != null ? asset.getDepreciationMethod().toString() : null);

        // Save conservation record
        FixedAssetConservation saved = conservationRepository.save(conservation);

        // Post conservation (change asset status)
        postConservation(saved.getConservationId());

        return saved;
    }

    /**
     * Cancel conservation and resume depreciation
     */
    public FixedAssetConservation cancelConservation(Long conservationId, String cancellationReason) {
        log.info("Cancelling conservation: {}", conservationId);

        FixedAssetConservation conservation = conservationRepository.findById(conservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conservation not found with id: " + conservationId));

        if (conservation.getIsCancelled()) {
            throw new BusinessException("Conservation is already cancelled");
        }

        if (!conservation.getIsPosted()) {
            throw new BusinessException("Conservation must be posted before it can be cancelled");
        }

        // Mark as cancelled
        conservation.setIsCancelled(true);
        conservation.setCancellationDate(java.time.LocalDate.now());
        conservation.setCancellationReason(cancellationReason);

        // Generate cancellation number
        String cancellationNumber = "CANCEL-" + conservation.getConservationNumber();
        conservation.setCancellationNumber(cancellationNumber);

        // Resume depreciation (change asset status back to ACTIVE)
        FixedAsset asset = conservation.getFixedAsset();
        asset.setStatus(AssetStatus.ACTIVE);
        fixedAssetRepository.save(asset);

        log.info("Cancelled conservation {} and resumed depreciation for asset {}",
                conservationId, asset.getFixedAssetId());

        return conservationRepository.save(conservation);
    }

    /**
     * Post conservation (change asset status to IN_CONSERVATION)
     */
    public void postConservation(Long conservationId) {
        log.info("Posting conservation: {}", conservationId);

        FixedAssetConservation conservation = conservationRepository.findById(conservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conservation not found with id: " + conservationId));

        if (conservation.getIsPosted()) {
            throw new BusinessException("Conservation is already posted");
        }

        // Change asset status to IN_CONSERVATION
        FixedAsset asset = conservation.getFixedAsset();
        asset.setStatus(AssetStatus.IN_CONSERVATION);
        fixedAssetRepository.save(asset);

        // Mark conservation as posted
        conservation.setIsPosted(true);
        conservation.setPostedDate(LocalDateTime.now());
        conservationRepository.save(conservation);

        log.info("Posted conservation {} and changed asset {} status to IN_CONSERVATION",
                conservationId, asset.getFixedAssetId());
    }

    /**
     * Get conservation by ID
     */
    @Transactional(readOnly = true)
    public FixedAssetConservation getById(Long id) {
        return conservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conservation not found with id: " + id));
    }

    /**
     * Get all conservations for an asset
     */
    @Transactional(readOnly = true)
    public List<FixedAssetConservation> getByAssetId(Long assetId) {
        return conservationRepository.findByFixedAssetId(assetId);
    }

    /**
     * Get active conservation for an asset
     */
    @Transactional(readOnly = true)
    public Optional<FixedAssetConservation> getActiveConservationByAssetId(Long assetId) {
        return conservationRepository.findActiveConservationByAssetId(assetId);
    }

    /**
     * Get all unposted conservations
     */
    @Transactional(readOnly = true)
    public List<FixedAssetConservation> getUnposted() {
        return conservationRepository.findUnposted();
    }

    /**
     * Get all active conservations
     */
    @Transactional(readOnly = true)
    public List<FixedAssetConservation> getActiveConservations() {
        return conservationRepository.findActiveConservations();
    }

    /**
     * Get all conservations
     */
    @Transactional(readOnly = true)
    public List<FixedAssetConservation> getAll() {
        return conservationRepository.findAll();
    }
}
