package com.example.accounts.service;

import com.example.accounts.dto.DepreciationScheduleDTO;
import com.example.accounts.dto.FixedAssetSummaryDTO;
import com.example.accounts.entity.FixedAsset;
import com.example.accounts.entity.FixedAssetDepreciation;
import com.example.accounts.entity.enums.AssetStatus;
import com.example.accounts.repository.FixedAssetDepreciationRepository;
import com.example.accounts.repository.FixedAssetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for Fixed Assets Reports and Dashboards
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FixedAssetReportService {

    private final FixedAssetRepository fixedAssetRepository;
    private final FixedAssetDepreciationRepository depreciationRepository;

    /**
     * Get summary/dashboard data
     */
    public FixedAssetSummaryDTO getSummary() {
        log.info("Generating fixed assets summary");

        List<FixedAsset> allAssets = fixedAssetRepository.findAll();

        FixedAssetSummaryDTO summary = new FixedAssetSummaryDTO();

        // Total counts
        summary.setTotalAssets((long) allAssets.size());

        // Financial totals
        BigDecimal totalGrossCost = BigDecimal.ZERO;
        BigDecimal totalAccumulatedDepreciation = BigDecimal.ZERO;
        BigDecimal totalNetBookValue = BigDecimal.ZERO;
        BigDecimal totalSalvageValue = BigDecimal.ZERO;

        // Status counts
        long newAssets = 0;
        long activeAssets = 0;
        long fullyDepreciatedAssets = 0;
        long heldForSaleAssets = 0;
        long disposedAssets = 0;
        long writtenOffAssets = 0;

        for (FixedAsset asset : allAssets) {
            totalGrossCost = totalGrossCost.add(asset.getGrossCost());
            totalAccumulatedDepreciation = totalAccumulatedDepreciation.add(asset.getAccumulatedDepreciation());
            totalNetBookValue = totalNetBookValue.add(asset.getNetBookValue());
            totalSalvageValue = totalSalvageValue
                    .add(asset.getSalvageValue() != null ? asset.getSalvageValue() : BigDecimal.ZERO);

            switch (asset.getStatus()) {
                case NEW -> newAssets++;
                case ACTIVE -> activeAssets++;
                case FULLY_DEPRECIATED -> fullyDepreciatedAssets++;
                case HELD_FOR_SALE -> heldForSaleAssets++;
                case DISPOSED -> disposedAssets++;
                case WRITTEN_OFF -> writtenOffAssets++;
            }
        }

        summary.setTotalGrossCost(totalGrossCost);
        summary.setTotalAccumulatedDepreciation(totalAccumulatedDepreciation);
        summary.setTotalNetBookValue(totalNetBookValue);
        summary.setTotalSalvageValue(totalSalvageValue);

        summary.setNewAssets(newAssets);
        summary.setActiveAssets(activeAssets);
        summary.setFullyDepreciatedAssets(fullyDepreciatedAssets);
        summary.setHeldForSaleAssets(heldForSaleAssets);
        summary.setDisposedAssets(disposedAssets);
        summary.setWrittenOffAssets(writtenOffAssets);

        // Calculate average depreciation rate
        if (totalGrossCost.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal avgRate = totalAccumulatedDepreciation
                    .divide(totalGrossCost, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            summary.setAverageDepreciationRate(avgRate);
        }

        // Current period depreciation
        LocalDate currentPeriod = LocalDate.now().withDayOfMonth(1);
        List<FixedAssetDepreciation> currentDepreciations = depreciationRepository
                .findByDepreciationPeriod(currentPeriod);
        BigDecimal currentPeriodDepreciation = currentDepreciations.stream()
                .map(FixedAssetDepreciation::getDepreciationAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.setCurrentPeriodDepreciation(currentPeriodDepreciation);

        // Assets needing depreciation
        List<FixedAsset> needingDepreciation = fixedAssetRepository.findAssetsNeedingDepreciation(currentPeriod);
        summary.setAssetsNeedingDepreciation((long) needingDepreciation.size());

        return summary;
    }

    /**
     * Get depreciation schedule for all active assets
     */
    public List<DepreciationScheduleDTO> getDepreciationSchedule() {
        log.info("Generating depreciation schedule");

        List<FixedAsset> activeAssets = fixedAssetRepository.findByStatus(AssetStatus.ACTIVE);

        return activeAssets.stream()
                .map(this::toDepreciationScheduleDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get depreciation schedule for specific asset
     */
    public DepreciationScheduleDTO getAssetDepreciationSchedule(Long assetId) {
        FixedAsset asset = fixedAssetRepository.findById(assetId)
                .orElseThrow(() -> new RuntimeException("Asset not found"));
        return toDepreciationScheduleDTO(asset);
    }

    /**
     * Get assets by class summary
     */
    public List<Object[]> getAssetsByClassSummary() {
        // This would use a custom query in the repository
        // For now, returning empty list
        return List.of();
    }

    /**
     * Convert asset to depreciation schedule DTO
     */
    private DepreciationScheduleDTO toDepreciationScheduleDTO(FixedAsset asset) {
        DepreciationScheduleDTO dto = new DepreciationScheduleDTO();

        dto.setAssetId(asset.getFixedAssetId());
        dto.setAssetNumber(asset.getAssetNumber());
        dto.setDescription(asset.getDescription());
        dto.setClassName(asset.getAssetClass() != null ? asset.getAssetClass().getDescription() : null);

        dto.setGrossCost(asset.getGrossCost());
        dto.setSalvageValue(asset.getSalvageValue());

        BigDecimal depreciableAmount = asset.getGrossCost().subtract(asset.getSalvageValue());
        dto.setDepreciableAmount(depreciableAmount);

        dto.setUsefulLifeMonths(asset.getUsefulLifeMonths());

        // Calculate remaining life
        if (asset.getDepreciationStartDate() != null && asset.getUsefulLifeMonths() != null) {
            LocalDate endDate = asset.getDepreciationStartDate().plusMonths(asset.getUsefulLifeMonths());
            long remainingMonths = ChronoUnit.MONTHS.between(LocalDate.now(), endDate);
            dto.setRemainingLifeMonths((int) Math.max(0, remainingMonths));
        }

        dto.setAccumulatedDepreciation(asset.getAccumulatedDepreciation());
        dto.setNetBookValue(asset.getNetBookValue());

        // Calculate monthly depreciation
        if (asset.getUsefulLifeMonths() != null && asset.getUsefulLifeMonths() > 0) {
            BigDecimal monthlyDepreciation = depreciableAmount.divide(
                    BigDecimal.valueOf(asset.getUsefulLifeMonths()),
                    4,
                    RoundingMode.HALF_UP);
            dto.setMonthlyDepreciation(monthlyDepreciation);
            dto.setAnnualDepreciation(monthlyDepreciation.multiply(BigDecimal.valueOf(12)));
        }

        dto.setDepreciationMethod(
                asset.getDepreciationMethod() != null ? asset.getDepreciationMethod().toString() : null);
        dto.setLastDepreciationDate(
                asset.getLastDepreciationDate() != null ? asset.getLastDepreciationDate().toString() : null);
        dto.setNextDepreciationDate(
                asset.getNextDepreciationDate() != null ? asset.getNextDepreciationDate().toString() : null);

        return dto;
    }

    /**
     * Get Statement of Depreciation Report
     * Groups assets by department and shows depreciation details
     */
    public List<com.example.accounts.dto.DepreciationStatementDTO> getDepreciationStatement() {
        log.info("Generating statement of depreciation");

        List<FixedAsset> allAssets = fixedAssetRepository.findAll();

        return allAssets.stream()
                .map(this::toDepreciationStatementDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get Document Expiration Report
     * Shows assets with expiring documents (insurance, certificates, etc.)
     */
    public List<com.example.accounts.dto.AssetDocumentExpirationDTO> getDocumentExpirations(LocalDate onDate) {
        log.info("Generating document expiration report for date: {}", onDate);

        // This would typically query a separate documents table
        // For now, returning empty list as placeholder
        // In a full implementation, you would have a FixedAssetDocument entity
        return List.of();
    }

    /**
     * Convert asset to depreciation statement DTO
     */
    private com.example.accounts.dto.DepreciationStatementDTO toDepreciationStatementDTO(FixedAsset asset) {
        com.example.accounts.dto.DepreciationStatementDTO dto = new com.example.accounts.dto.DepreciationStatementDTO();

        dto.setDepartment(asset.getDepartment());
        dto.setFixedAssetId(asset.getFixedAssetId());
        dto.setAssetNumber(asset.getAssetNumber());
        dto.setAssetDescription(asset.getDescription());

        dto.setDepreciationMethod(
                asset.getDepreciationMethod() != null ? asset.getDepreciationMethod().toString() : null);

        dto.setInitialCost(asset.getInitialCost());
        dto.setSalvageValue(asset.getSalvageValue());
        dto.setCostAdjustment(asset.getCostAdjustment());

        // Get opening balance (accumulated depreciation at start of period)
        // This would typically query the depreciation history
        dto.setOpeningBalance(BigDecimal.ZERO);

        // Get current period depreciation
        LocalDate currentPeriod = LocalDate.now().withDayOfMonth(1);
        List<FixedAssetDepreciation> periodDepreciations = depreciationRepository
                .findByFixedAssetIdAndPeriod(asset.getFixedAssetId(), currentPeriod)
                .map(List::of)
                .orElse(List.of());

        BigDecimal depreciationChanges = periodDepreciations.stream()
                .map(FixedAssetDepreciation::getDepreciationAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        dto.setDepreciationChanges(depreciationChanges);
        dto.setClosingBalance(asset.getAccumulatedDepreciation());
        dto.setNetBookValue(asset.getNetBookValue());

        return dto;
    }
}
