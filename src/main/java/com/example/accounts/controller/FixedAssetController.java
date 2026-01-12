package com.example.accounts.controller;

import com.example.accounts.dto.FixedAssetRequest;
import com.example.accounts.dto.FixedAssetResponse;
import com.example.accounts.entity.enums.AssetStatus;
import com.example.accounts.service.FixedAssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for Fixed Assets
 */
@RestController
@RequestMapping("/api/fixed-assets")
@RequiredArgsConstructor
@Tag(name = "Fixed Assets", description = "Manage fixed assets and their lifecycle")
public class FixedAssetController {

    private final FixedAssetService fixedAssetService;

    @PostMapping
    @Operation(summary = "Create fixed asset", description = "Create a new fixed asset")
    public ResponseEntity<FixedAssetResponse> createAsset(@RequestBody FixedAssetRequest request) {
        FixedAssetResponse response = fixedAssetService.createAsset(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{assetId}")
    @Operation(summary = "Get asset by ID", description = "Retrieve a fixed asset by its ID")
    public ResponseEntity<FixedAssetResponse> getAssetById(@PathVariable Long assetId) {
        FixedAssetResponse response = fixedAssetService.getAssetById(assetId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/number/{assetNumber}")
    @Operation(summary = "Get asset by number", description = "Retrieve a fixed asset by its asset number")
    public ResponseEntity<FixedAssetResponse> getAssetByNumber(@PathVariable String assetNumber) {
        FixedAssetResponse response = fixedAssetService.getAssetByNumber(assetNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all assets", description = "Retrieve all fixed assets")
    public ResponseEntity<List<FixedAssetResponse>> getAllAssets() {
        List<FixedAssetResponse> response = fixedAssetService.getAllAssets();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get assets by status", description = "Retrieve fixed assets by status")
    public ResponseEntity<List<FixedAssetResponse>> getAssetsByStatus(@PathVariable AssetStatus status) {
        List<FixedAssetResponse> response = fixedAssetService.getAssetsByStatus(status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/class/{classId}")
    @Operation(summary = "Get assets by class", description = "Retrieve fixed assets by class ID")
    public ResponseEntity<List<FixedAssetResponse>> getAssetsByClass(@PathVariable Long classId) {
        List<FixedAssetResponse> response = fixedAssetService.getAssetsByClass(classId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/folder/{folder}")
    @Operation(summary = "Get assets by folder", description = "Retrieve fixed assets by folder")
    public ResponseEntity<List<FixedAssetResponse>> getAssetsByFolder(@PathVariable String folder) {
        List<FixedAssetResponse> response = fixedAssetService.getAssetsByFolder(folder);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/needing-depreciation")
    @Operation(summary = "Get assets needing depreciation", description = "Retrieve assets that need depreciation for a given period")
    public ResponseEntity<List<FixedAssetResponse>> getAssetsNeedingDepreciation(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate targetPeriod) {
        List<FixedAssetResponse> response = fixedAssetService.getAssetsNeedingDepreciation(targetPeriod);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{assetId}")
    @Operation(summary = "Update asset", description = "Update an existing fixed asset")
    public ResponseEntity<FixedAssetResponse> updateAsset(
            @PathVariable Long assetId,
            @RequestBody FixedAssetRequest request) {
        FixedAssetResponse response = fixedAssetService.updateAsset(assetId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{assetId}/status")
    @Operation(summary = "Change asset status", description = "Change the status of a fixed asset")
    public ResponseEntity<FixedAssetResponse> changeStatus(
            @PathVariable Long assetId,
            @RequestParam AssetStatus newStatus) {
        FixedAssetResponse response = fixedAssetService.changeStatus(assetId, newStatus);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{assetId}")
    @Operation(summary = "Delete asset", description = "Delete a fixed asset (only NEW assets)")
    public ResponseEntity<Void> deleteAsset(@PathVariable Long assetId) {
        fixedAssetService.deleteAsset(assetId);
        return ResponseEntity.noContent().build();
    }
}
