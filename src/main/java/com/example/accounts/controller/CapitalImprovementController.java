package com.example.accounts.controller;

import com.example.accounts.entity.FixedAssetCapitalImprovement;
import com.example.accounts.service.CapitalImprovementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Capital Improvements
 */
@RestController
@RequestMapping("/api/capital-improvements")
@RequiredArgsConstructor
@Tag(name = "Capital Improvements", description = "Manage capital improvements to fixed assets")
public class CapitalImprovementController {

    private final CapitalImprovementService improvementService;

    @PostMapping
    @Operation(summary = "Create improvement", description = "Create a capital improvement")
    public ResponseEntity<FixedAssetCapitalImprovement> createImprovement(
            @RequestBody FixedAssetCapitalImprovement improvement) {
        FixedAssetCapitalImprovement created = improvementService.createImprovement(improvement);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/{improvementId}/post")
    @Operation(summary = "Post improvement", description = "Post improvement (update asset cost and useful life)")
    public ResponseEntity<Void> postImprovement(@PathVariable Long improvementId) {
        improvementService.postImprovement(improvementId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{improvementId}")
    @Operation(summary = "Get improvement by ID", description = "Retrieve a capital improvement by ID")
    public ResponseEntity<FixedAssetCapitalImprovement> getImprovementById(@PathVariable Long improvementId) {
        FixedAssetCapitalImprovement improvement = improvementService.getImprovementById(improvementId);
        return ResponseEntity.ok(improvement);
    }

    @GetMapping("/asset/{assetId}")
    @Operation(summary = "Get improvements by asset", description = "Get all improvements for an asset")
    public ResponseEntity<List<FixedAssetCapitalImprovement>> getImprovementsByAsset(@PathVariable Long assetId) {
        List<FixedAssetCapitalImprovement> improvements = improvementService.getImprovementsByAsset(assetId);
        return ResponseEntity.ok(improvements);
    }

    @GetMapping("/unposted")
    @Operation(summary = "Get unposted improvements", description = "Get all unposted improvements")
    public ResponseEntity<List<FixedAssetCapitalImprovement>> getUnpostedImprovements() {
        List<FixedAssetCapitalImprovement> improvements = improvementService.getUnpostedImprovements();
        return ResponseEntity.ok(improvements);
    }
}
