package com.example.accounts.service;

import com.example.accounts.entity.FixedAsset;
import com.example.accounts.entity.FixedAssetSale;
import com.example.accounts.entity.FixedAssetSalePreparation;
import com.example.accounts.entity.enums.AssetStatus;
import com.example.accounts.exception.BusinessException;
import com.example.accounts.exception.ResourceNotFoundException;
import com.example.accounts.repository.FixedAssetRepository;
import com.example.accounts.repository.FixedAssetSalePreparationRepository;
import com.example.accounts.repository.FixedAssetSaleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for Fixed Asset Sales (Disposal)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SaleService {

    private final FixedAssetSaleRepository saleRepository;
    private final FixedAssetRepository fixedAssetRepository;
    private final FixedAssetSalePreparationRepository salePreparationRepository;

    /**
     * Create sale
     */
    public FixedAssetSale createSale(FixedAssetSale sale) {
        log.info("Creating sale for asset: {}", sale.getFixedAsset().getFixedAssetId());

        FixedAsset asset = sale.getFixedAsset();

        // Validate asset can be sold
        if (asset.getStatus() != AssetStatus.HELD_FOR_SALE) {
            throw new BusinessException("Asset must be HELD_FOR_SALE to sell. Current status: " + asset.getStatus());
        }

        // Validate sale price
        if (sale.getSalePrice() == null || sale.getSalePrice().signum() < 0) {
            throw new BusinessException("Sale price must be zero or greater");
        }

        // Record financial details at sale
        sale.setGrossCostAtSale(asset.getGrossCost());
        sale.setAccumulatedDepreciationAtSale(asset.getAccumulatedDepreciation());
        sale.setNetBookValueAtSale(asset.getNetBookValue());

        // Link to preparation if exists
        if (sale.getPreparation() != null) {
            FixedAssetSalePreparation preparation = sale.getPreparation();
            preparation.setActualSale(sale);
            salePreparationRepository.save(preparation);
        }

        FixedAssetSale saved = saleRepository.save(sale);

        // Calculate gain/loss
        BigDecimal gainLoss = saved.getGainLossOnSale();
        log.info("Created sale: {} with {} of {}",
                saved.getSaleId(),
                gainLoss.signum() >= 0 ? "gain" : "loss",
                gainLoss.abs());

        return saved;
    }

    /**
     * Post sale (change status to DISPOSED)
     */
    public void postSale(Long saleId) {
        log.info("Posting sale: {}", saleId);

        FixedAssetSale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with id: " + saleId));

        if (sale.getIsPosted()) {
            throw new BusinessException("Sale is already posted");
        }

        FixedAsset asset = sale.getFixedAsset();

        // Change status to DISPOSED
        asset.setStatus(AssetStatus.DISPOSED);
        asset.setDisposalDate(sale.getSaleDate());

        // Fully depreciate the asset
        asset.setAccumulatedDepreciation(asset.getGrossCost());

        fixedAssetRepository.save(asset);

        // Mark sale as posted
        sale.setIsPosted(true);
        sale.setPostedDate(LocalDateTime.now());

        saleRepository.save(sale);

        BigDecimal gainLoss = sale.getGainLossOnSale();
        log.info("Posted sale {} for asset {}. {} on disposal: {}",
                saleId,
                asset.getFixedAssetId(),
                gainLoss.signum() >= 0 ? "Gain" : "Loss",
                gainLoss.abs());
    }

    /**
     * Get sale by ID
     */
    @Transactional(readOnly = true)
    public FixedAssetSale getSaleById(Long saleId) {
        return saleRepository.findById(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with id: " + saleId));
    }

    /**
     * Get sales by asset
     */
    @Transactional(readOnly = true)
    public List<FixedAssetSale> getSalesByAsset(Long assetId) {
        return saleRepository.findByFixedAssetId(assetId);
    }

    /**
     * Get sales by buyer
     */
    @Transactional(readOnly = true)
    public List<FixedAssetSale> getSalesByBuyer(Long buyerId) {
        return saleRepository.findByBuyerId(buyerId);
    }

    /**
     * Get unposted sales
     */
    @Transactional(readOnly = true)
    public List<FixedAssetSale> getUnpostedSales() {
        return saleRepository.findByIsPostedFalse();
    }
}
