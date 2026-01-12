package com.example.accounts.service;

import com.example.accounts.dto.FixedAssetRequest;
import com.example.accounts.dto.FixedAssetResponse;
import com.example.accounts.entity.*;
import com.example.accounts.entity.enums.AssetStatus;
import com.example.accounts.exception.BusinessException;
import com.example.accounts.exception.ResourceNotFoundException;
import com.example.accounts.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for Fixed Asset operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FixedAssetService {

    private final FixedAssetRepository fixedAssetRepository;
    private final FixedAssetClassRepository fixedAssetClassRepository;
    private final ChartOfAccountRepository chartOfAccountRepository;
    private final ItemRepository itemRepository;
    private final ExpenseItemRepository expenseItemRepository;

    /**
     * Create a new fixed asset
     */
    public FixedAssetResponse createAsset(FixedAssetRequest request) {
        log.info("Creating fixed asset: {}", request.getDescription());

        // Validate required fields
        validateAssetRequest(request);

        FixedAsset asset = new FixedAsset();
        mapRequestToEntity(request, asset);

        // Set default status
        asset.setStatus(request.getStatus() != null ? request.getStatus() : AssetStatus.NEW);

        FixedAsset saved = fixedAssetRepository.save(asset);
        log.info("Created fixed asset with id: {} and number: {}", saved.getFixedAssetId(), saved.getAssetNumber());

        return toResponse(saved);
    }

    /**
     * Get asset by ID
     */
    @Transactional(readOnly = true)
    public FixedAssetResponse getAssetById(Long assetId) {
        FixedAsset asset = findAssetById(assetId);
        return toResponse(asset);
    }

    /**
     * Get asset by asset number
     */
    @Transactional(readOnly = true)
    public FixedAssetResponse getAssetByNumber(String assetNumber) {
        FixedAsset asset = fixedAssetRepository.findByAssetNumber(assetNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Fixed asset not found with number: " + assetNumber));
        return toResponse(asset);
    }

    /**
     * Get all assets
     */
    @Transactional(readOnly = true)
    public List<FixedAssetResponse> getAllAssets() {
        return fixedAssetRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get assets by status
     */
    @Transactional(readOnly = true)
    public List<FixedAssetResponse> getAssetsByStatus(AssetStatus status) {
        return fixedAssetRepository.findByStatus(status).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get assets by class
     */
    @Transactional(readOnly = true)
    public List<FixedAssetResponse> getAssetsByClass(Long classId) {
        return fixedAssetRepository.findByClassId(classId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get assets by folder
     */
    @Transactional(readOnly = true)
    public List<FixedAssetResponse> getAssetsByFolder(String folder) {
        return fixedAssetRepository.findByFolder(folder).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get assets needing depreciation
     */
    @Transactional(readOnly = true)
    public List<FixedAssetResponse> getAssetsNeedingDepreciation(LocalDate targetPeriod) {
        return fixedAssetRepository.findAssetsNeedingDepreciation(targetPeriod).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update asset
     */
    public FixedAssetResponse updateAsset(Long assetId, FixedAssetRequest request) {
        log.info("Updating fixed asset: {}", assetId);

        FixedAsset asset = findAssetById(assetId);

        // Validate status transition if status is being changed
        if (request.getStatus() != null && !request.getStatus().equals(asset.getStatus())) {
            validateStatusTransition(asset.getStatus(), request.getStatus());
        }

        mapRequestToEntity(request, asset);

        FixedAsset updated = fixedAssetRepository.save(asset);
        log.info("Updated fixed asset: {}", assetId);

        return toResponse(updated);
    }

    /**
     * Change asset status
     */
    public FixedAssetResponse changeStatus(Long assetId, AssetStatus newStatus) {
        log.info("Changing status of asset {} to {}", assetId, newStatus);

        FixedAsset asset = findAssetById(assetId);
        validateStatusTransition(asset.getStatus(), newStatus);

        asset.setStatus(newStatus);

        // Set activation date when moving to ACTIVE
        if (newStatus == AssetStatus.ACTIVE && asset.getActivationDate() == null) {
            asset.setActivationDate(LocalDate.now());
        }

        // Set disposal date when moving to DISPOSED
        if (newStatus == AssetStatus.DISPOSED && asset.getDisposalDate() == null) {
            asset.setDisposalDate(LocalDate.now());
        }

        FixedAsset updated = fixedAssetRepository.save(asset);
        log.info("Changed status of asset {} to {}", assetId, newStatus);

        return toResponse(updated);
    }

    /**
     * Delete asset
     */
    public void deleteAsset(Long assetId) {
        log.info("Deleting fixed asset: {}", assetId);

        FixedAsset asset = findAssetById(assetId);

        // Only allow deletion of NEW assets
        if (asset.getStatus() != AssetStatus.NEW) {
            throw new BusinessException(
                    "Cannot delete asset with status: " + asset.getStatus() + ". Only NEW assets can be deleted.");
        }

        fixedAssetRepository.deleteById(assetId);
        log.info("Deleted fixed asset: {}", assetId);
    }

    /**
     * Validate asset request
     */
    private void validateAssetRequest(FixedAssetRequest request) {
        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            throw new BusinessException("Asset description is required");
        }

        if (request.getClassId() == null) {
            throw new BusinessException("Asset class is required");
        }

        if (request.getDepreciationMethod() == null) {
            throw new BusinessException("Depreciation method is required");
        }

        if (request.getGlAccountId() == null || request.getDepreciationGlAccountId() == null
                || request.getExpenseGlAccountId() == null) {
            throw new BusinessException("GL accounts (Asset, Depreciation, Expense) are required");
        }
    }

    /**
     * Validate status transition
     */
    private void validateStatusTransition(AssetStatus currentStatus, AssetStatus newStatus) {
        // Define valid transitions
        boolean isValid = switch (currentStatus) {
            case NEW -> newStatus == AssetStatus.ACTIVE || newStatus == AssetStatus.CONSTRUCTION_IN_PROGRESS;
            case CONSTRUCTION_IN_PROGRESS -> newStatus == AssetStatus.CONSTRUCTION_COMPLETED;
            case CONSTRUCTION_COMPLETED -> newStatus == AssetStatus.ACTIVE;
            case ACTIVE -> newStatus == AssetStatus.FULLY_DEPRECIATED || newStatus == AssetStatus.HELD_FOR_SALE
                    || newStatus == AssetStatus.WRITTEN_OFF || newStatus == AssetStatus.IN_CONSERVATION;
            case IN_CONSERVATION -> newStatus == AssetStatus.ACTIVE; // Can only return to ACTIVE
            case FULLY_DEPRECIATED -> newStatus == AssetStatus.HELD_FOR_SALE || newStatus == AssetStatus.WRITTEN_OFF;
            case HELD_FOR_SALE -> newStatus == AssetStatus.DISPOSED || newStatus == AssetStatus.ACTIVE;
            case DISPOSED, WRITTEN_OFF -> false; // Terminal states
        };

        if (!isValid) {
            throw new BusinessException("Invalid status transition from " + currentStatus + " to " + newStatus);
        }
    }

    /**
     * Map request to entity
     */
    private void mapRequestToEntity(FixedAssetRequest request, FixedAsset asset) {
        if (request.getAssetNumber() != null) {
            asset.setAssetNumber(request.getAssetNumber());
        }
        if (request.getDescription() != null) {
            asset.setDescription(request.getDescription());
        }
        if (request.getFolder() != null) {
            asset.setFolder(request.getFolder());
        }
        if (request.getCategory() != null) {
            asset.setCategory(request.getCategory());
        }
        if (request.getLocation() != null) {
            asset.setLocation(request.getLocation());
        }
        if (request.getDepartment() != null) {
            asset.setDepartment(request.getDepartment());
        }

        // Set class
        if (request.getClassId() != null) {
            FixedAssetClass assetClass = fixedAssetClassRepository.findById(request.getClassId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Asset class not found with id: " + request.getClassId()));
            asset.setAssetClass(assetClass);
        }

        // Set linked items
        if (request.getItemId() != null) {
            Item item = itemRepository.findById(request.getItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + request.getItemId()));
            asset.setItem(item);
        }

        if (request.getExpenseItemId() != null) {
            ExpenseItem expenseItem = expenseItemRepository.findById(request.getExpenseItemId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Expense item not found with id: " + request.getExpenseItemId()));
            asset.setExpenseItem(expenseItem);
        }

        // Set financial details
        if (request.getInitialCost() != null) {
            asset.setInitialCost(request.getInitialCost());
        }
        if (request.getSalvageValue() != null) {
            asset.setSalvageValue(request.getSalvageValue());
        }

        // Set depreciation settings
        if (request.getDepreciationMethod() != null) {
            asset.setDepreciationMethod(request.getDepreciationMethod());
        }
        if (request.getUsefulLifeMonths() != null) {
            asset.setUsefulLifeMonths(request.getUsefulLifeMonths());
        }
        if (request.getTotalUnits() != null) {
            asset.setTotalUnits(request.getTotalUnits());
            asset.setRemainingUnits(request.getTotalUnits()); // Initialize remaining units
        }

        // Set GL accounts
        if (request.getGlAccountId() != null) {
            ChartOfAccount glAccount = chartOfAccountRepository.findById(request.getGlAccountId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "GL account not found with id: " + request.getGlAccountId()));
            asset.setGlAccount(glAccount);
        }

        if (request.getDepreciationGlAccountId() != null) {
            ChartOfAccount depreciationGlAccount = chartOfAccountRepository
                    .findById(request.getDepreciationGlAccountId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Depreciation GL account not found with id: " + request.getDepreciationGlAccountId()));
            asset.setDepreciationGlAccount(depreciationGlAccount);
        }

        if (request.getExpenseGlAccountId() != null) {
            ChartOfAccount expenseGlAccount = chartOfAccountRepository.findById(request.getExpenseGlAccountId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Expense GL account not found with id: " + request.getExpenseGlAccountId()));
            asset.setExpenseGlAccount(expenseGlAccount);
        }

        if (request.getHeldForSaleGlAccountId() != null) {
            ChartOfAccount heldForSaleGlAccount = chartOfAccountRepository.findById(request.getHeldForSaleGlAccountId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Held for sale GL account not found with id: " + request.getHeldForSaleGlAccountId()));
            asset.setHeldForSaleGlAccount(heldForSaleGlAccount);
        }

        if (request.getConstructionInProgressGlAccountId() != null) {
            ChartOfAccount cipGlAccount = chartOfAccountRepository
                    .findById(request.getConstructionInProgressGlAccountId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Construction in progress GL account not found with id: "
                                    + request.getConstructionInProgressGlAccountId()));
            asset.setConstructionInProgressGlAccount(cipGlAccount);
        }

        if (request.getCapitalImprovementsGlAccountId() != null) {
            ChartOfAccount capitalImprovementsGlAccount = chartOfAccountRepository
                    .findById(request.getCapitalImprovementsGlAccountId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Capital improvements GL account not found with id: "
                                    + request.getCapitalImprovementsGlAccountId()));
            asset.setCapitalImprovementsGlAccount(capitalImprovementsGlAccount);
        }

        // Set dates
        if (request.getAcquisitionDate() != null) {
            asset.setAcquisitionDate(request.getAcquisitionDate());
        }

        // Set status
        if (request.getStatus() != null) {
            asset.setStatus(request.getStatus());
        }

        if (request.getUseScheduling() != null) {
            asset.setUseScheduling(request.getUseScheduling());
        }
    }

    /**
     * Convert entity to response DTO
     */
    private FixedAssetResponse toResponse(FixedAsset asset) {
        FixedAssetResponse response = new FixedAssetResponse();
        response.setFixedAssetId(asset.getFixedAssetId());
        response.setAssetNumber(asset.getAssetNumber());
        response.setDescription(asset.getDescription());
        response.setFolder(asset.getFolder());

        // Classification
        if (asset.getAssetClass() != null) {
            response.setClassId(asset.getAssetClass().getClassId());
            response.setClassName(asset.getAssetClass().getDescription());
        }
        response.setCategory(asset.getCategory());

        // Location
        response.setLocation(asset.getLocation());
        response.setDepartment(asset.getDepartment());

        // Financial details
        response.setInitialCost(asset.getInitialCost());
        response.setCostAdjustment(asset.getCostAdjustment());
        response.setGrossCost(asset.getGrossCost());
        response.setAccumulatedDepreciation(asset.getAccumulatedDepreciation());
        response.setNetBookValue(asset.getNetBookValue());
        response.setSalvageValue(asset.getSalvageValue());

        // Depreciation settings
        response.setDepreciationMethod(asset.getDepreciationMethod());
        response.setUsefulLifeMonths(asset.getUsefulLifeMonths());
        response.setTotalUnits(asset.getTotalUnits());
        response.setRemainingUnits(asset.getRemainingUnits());

        // Depreciation tracking
        response.setDepreciationStartDate(asset.getDepreciationStartDate());
        response.setLastDepreciationDate(asset.getLastDepreciationDate());
        response.setLastDepreciationCalculationDate(asset.getLastDepreciationCalculationDate());
        response.setNextDepreciationDate(asset.getNextDepreciationDate());

        // Status
        response.setStatus(asset.getStatus());

        // Dates
        response.setAcquisitionDate(asset.getAcquisitionDate());
        response.setActivationDate(asset.getActivationDate());
        response.setDisposalDate(asset.getDisposalDate());

        return response;
    }

    /**
     * Find asset by ID or throw exception
     */
    private FixedAsset findAssetById(Long assetId) {
        return fixedAssetRepository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Fixed asset not found with id: " + assetId));
    }
}
