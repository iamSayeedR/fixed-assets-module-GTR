package com.example.accounts.service;

import com.example.accounts.entity.FixedAsset;
import com.example.accounts.entity.FixedAssetWriteOff;
import com.example.accounts.entity.enums.AssetStatus;
import com.example.accounts.exception.BusinessException;
import com.example.accounts.exception.ResourceNotFoundException;
import com.example.accounts.repository.FixedAssetRepository;
import com.example.accounts.repository.FixedAssetWriteOffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for Fixed Asset Write-Offs
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WriteOffService {

    private final FixedAssetWriteOffRepository writeOffRepository;
    private final FixedAssetRepository fixedAssetRepository;

    /**
     * Create write-off
     */
    public FixedAssetWriteOff createWriteOff(FixedAssetWriteOff writeOff) {
        log.info("Creating write-off for asset: {}", writeOff.getFixedAsset().getFixedAssetId());

        FixedAsset asset = writeOff.getFixedAsset();

        // Validate asset can be written off
        if (asset.getStatus() != AssetStatus.ACTIVE && asset.getStatus() != AssetStatus.FULLY_DEPRECIATED) {
            throw new BusinessException(
                    "Asset must be ACTIVE or FULLY_DEPRECIATED to write off. Current status: " + asset.getStatus());
        }

        // Record financial details at write-off
        writeOff.setGrossCostAtWriteOff(asset.getGrossCost());
        writeOff.setAccumulatedDepreciationAtWriteOff(asset.getAccumulatedDepreciation());
        writeOff.setNetBookValueAtWriteOff(asset.getNetBookValue());

        // Calculate loss (NBV is the loss amount)
        writeOff.setLossAmount(asset.getNetBookValue());

        FixedAssetWriteOff saved = writeOffRepository.save(writeOff);

        log.info("Created write-off: {} with loss amount: {}", saved.getWriteOffId(), saved.getLossAmount());

        return saved;
    }

    /**
     * Post write-off (change asset status to WRITTEN_OFF)
     */
    public void postWriteOff(Long writeOffId) {
        log.info("Posting write-off: {}", writeOffId);

        FixedAssetWriteOff writeOff = writeOffRepository.findById(writeOffId)
                .orElseThrow(() -> new ResourceNotFoundException("Write-off not found with id: " + writeOffId));

        if (writeOff.getIsPosted()) {
            throw new BusinessException("Write-off is already posted");
        }

        FixedAsset asset = writeOff.getFixedAsset();

        // Change status to WRITTEN_OFF
        asset.setStatus(AssetStatus.WRITTEN_OFF);
        asset.setDisposalDate(writeOff.getWriteOffDate());

        // Fully depreciate the asset (set accumulated depreciation = gross cost)
        asset.setAccumulatedDepreciation(asset.getGrossCost());

        fixedAssetRepository.save(asset);

        // Mark write-off as posted
        writeOff.setIsPosted(true);
        writeOff.setPostedDate(LocalDateTime.now());

        writeOffRepository.save(writeOff);

        log.info("Posted write-off {} for asset {}", writeOffId, asset.getFixedAssetId());
    }

    /**
     * Get write-off by ID
     */
    @Transactional(readOnly = true)
    public FixedAssetWriteOff getWriteOffById(Long writeOffId) {
        return writeOffRepository.findById(writeOffId)
                .orElseThrow(() -> new ResourceNotFoundException("Write-off not found with id: " + writeOffId));
    }

    /**
     * Get write-offs by asset
     */
    @Transactional(readOnly = true)
    public List<FixedAssetWriteOff> getWriteOffsByAsset(Long assetId) {
        return writeOffRepository.findByFixedAssetId(assetId);
    }

    /**
     * Get unposted write-offs
     */
    @Transactional(readOnly = true)
    public List<FixedAssetWriteOff> getUnpostedWriteOffs() {
        return writeOffRepository.findByIsPostedFalse();
    }
}
