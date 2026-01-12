package com.example.accounts.controller;

import com.example.accounts.entity.FixedAssetDepreciation;
import com.example.accounts.service.DepreciationService;
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
 * REST Controller for Fixed Asset Depreciation
 */
@RestController
@RequestMapping("/api/depreciation")
@RequiredArgsConstructor
@Tag(name = "Depreciation", description = "Calculate and manage fixed asset depreciation")
public class DepreciationController {

    private final DepreciationService depreciationService;

    @PostMapping("/calculate")
    @Operation(summary = "Calculate depreciation", description = "Calculate depreciation for a specific asset and period")
    public ResponseEntity<FixedAssetDepreciation> calculateDepreciation(
            @RequestParam Long assetId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period) {
        FixedAssetDepreciation depreciation = depreciationService.calculateDepreciation(assetId, period);
        return ResponseEntity.status(HttpStatus.CREATED).body(depreciation);
    }

    @PostMapping("/calculate-monthly")
    @Operation(summary = "Calculate monthly depreciation", description = "Calculate depreciation for all active assets for a given period")
    public ResponseEntity<List<FixedAssetDepreciation>> calculateMonthlyDepreciation(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period) {
        List<FixedAssetDepreciation> depreciations = depreciationService.calculateMonthlyDepreciation(period);
        return ResponseEntity.status(HttpStatus.CREATED).body(depreciations);
    }

    @PostMapping("/{depreciationId}/post")
    @Operation(summary = "Post depreciation", description = "Post depreciation to GL (create journal entry)")
    public ResponseEntity<Void> postDepreciation(@PathVariable Long depreciationId) {
        depreciationService.postDepreciation(depreciationId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/asset/{assetId}")
    @Operation(summary = "Get depreciation history", description = "Get depreciation history for an asset")
    public ResponseEntity<List<FixedAssetDepreciation>> getDepreciationHistory(@PathVariable Long assetId) {
        List<FixedAssetDepreciation> history = depreciationService.getDepreciationHistory(assetId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/period/{period}")
    @Operation(summary = "Get depreciation by period", description = "Get all depreciation records for a specific period")
    public ResponseEntity<List<FixedAssetDepreciation>> getDepreciationByPeriod(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period) {
        List<FixedAssetDepreciation> depreciations = depreciationService.getDepreciationByPeriod(period);
        return ResponseEntity.ok(depreciations);
    }
}
