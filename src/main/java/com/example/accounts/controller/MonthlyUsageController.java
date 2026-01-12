package com.example.accounts.controller;

import com.example.accounts.entity.FixedAssetMonthlyUsage;
import com.example.accounts.service.MonthlyUsageService;
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
 * REST Controller for Monthly Usage (Units of Production)
 */
@RestController
@RequestMapping("/api/monthly-usage")
@RequiredArgsConstructor
@Tag(name = "Monthly Usage", description = "Track monthly usage for Units of Production depreciation")
public class MonthlyUsageController {

    private final MonthlyUsageService usageService;

    @PostMapping
    @Operation(summary = "Record usage", description = "Record monthly usage for an asset")
    public ResponseEntity<FixedAssetMonthlyUsage> recordUsage(
            @RequestParam Long assetId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period,
            @RequestParam Integer unitsUsed,
            @RequestParam(required = false) String notes) {
        FixedAssetMonthlyUsage usage = usageService.recordUsage(assetId, period, unitsUsed, notes);
        return ResponseEntity.status(HttpStatus.CREATED).body(usage);
    }

    @PostMapping("/{usageId}/process")
    @Operation(summary = "Process usage", description = "Mark usage as processed (ready for depreciation)")
    public ResponseEntity<Void> processUsage(@PathVariable Long usageId) {
        usageService.processUsage(usageId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{usageId}")
    @Operation(summary = "Get usage by ID", description = "Retrieve usage record by ID")
    public ResponseEntity<FixedAssetMonthlyUsage> getUsageById(@PathVariable Long usageId) {
        FixedAssetMonthlyUsage usage = usageService.getUsageById(usageId);
        return ResponseEntity.ok(usage);
    }

    @GetMapping("/asset/{assetId}")
    @Operation(summary = "Get usage by asset", description = "Get usage history for an asset")
    public ResponseEntity<List<FixedAssetMonthlyUsage>> getUsageByAsset(@PathVariable Long assetId) {
        List<FixedAssetMonthlyUsage> usage = usageService.getUsageByAsset(assetId);
        return ResponseEntity.ok(usage);
    }

    @GetMapping("/unprocessed")
    @Operation(summary = "Get unprocessed usage", description = "Get all unprocessed usage records")
    public ResponseEntity<List<FixedAssetMonthlyUsage>> getUnprocessedUsage() {
        List<FixedAssetMonthlyUsage> usage = usageService.getUnprocessedUsage();
        return ResponseEntity.ok(usage);
    }
}
