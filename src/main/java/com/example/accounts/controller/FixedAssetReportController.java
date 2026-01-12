package com.example.accounts.controller;

import com.example.accounts.dto.DepreciationScheduleDTO;
import com.example.accounts.dto.FixedAssetSummaryDTO;
import com.example.accounts.service.FixedAssetReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Fixed Assets Reports and Dashboards
 */
@RestController
@RequestMapping("/api/fixed-assets/reports")
@RequiredArgsConstructor
@Tag(name = "Fixed Assets Reports", description = "Reports, dashboards, and analytics for fixed assets")
public class FixedAssetReportController {

    private final FixedAssetReportService reportService;

    @GetMapping("/summary")
    @Operation(summary = "Get summary dashboard", description = "Get comprehensive summary of all fixed assets")
    public ResponseEntity<FixedAssetSummaryDTO> getSummary() {
        FixedAssetSummaryDTO summary = reportService.getSummary();
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/depreciation-schedule")
    @Operation(summary = "Get depreciation schedule", description = "Get depreciation schedule for all active assets")
    public ResponseEntity<List<DepreciationScheduleDTO>> getDepreciationSchedule() {
        List<DepreciationScheduleDTO> schedule = reportService.getDepreciationSchedule();
        return ResponseEntity.ok(schedule);
    }

    @GetMapping("/depreciation-schedule/{assetId}")
    @Operation(summary = "Get asset depreciation schedule", description = "Get depreciation schedule for a specific asset")
    public ResponseEntity<DepreciationScheduleDTO> getAssetDepreciationSchedule(@PathVariable Long assetId) {
        DepreciationScheduleDTO schedule = reportService.getAssetDepreciationSchedule(assetId);
        return ResponseEntity.ok(schedule);
    }

    @GetMapping("/depreciation-statement")
    @Operation(summary = "Get statement of depreciation", description = "Get comprehensive depreciation statement grouped by department")
    public ResponseEntity<List<com.example.accounts.dto.DepreciationStatementDTO>> getDepreciationStatement() {
        List<com.example.accounts.dto.DepreciationStatementDTO> statement = reportService.getDepreciationStatement();
        return ResponseEntity.ok(statement);
    }

    @GetMapping("/document-expirations")
    @Operation(summary = "Get document expirations", description = "Get assets with expiring documents (insurance, certificates, maintenance)")
    public ResponseEntity<List<com.example.accounts.dto.AssetDocumentExpirationDTO>> getDocumentExpirations(
            @RequestParam(required = false) String onDate) {
        java.time.LocalDate date = onDate != null ? java.time.LocalDate.parse(onDate) : java.time.LocalDate.now();
        List<com.example.accounts.dto.AssetDocumentExpirationDTO> expirations = reportService
                .getDocumentExpirations(date);
        return ResponseEntity.ok(expirations);
    }
}
