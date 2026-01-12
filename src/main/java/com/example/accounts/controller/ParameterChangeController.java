package com.example.accounts.controller;

import com.example.accounts.entity.FixedAssetParameterChange;
import com.example.accounts.service.ParameterChangeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Parameter Changes (Reassessments)
 */
@RestController
@RequestMapping("/api/parameter-changes")
@RequiredArgsConstructor
@Tag(name = "Parameter Changes", description = "Manage asset reassessments and parameter changes")
public class ParameterChangeController {

    private final ParameterChangeService parameterChangeService;

    @PostMapping
    @Operation(summary = "Create parameter change", description = "Create a parameter change (impairment, revaluation, etc.)")
    public ResponseEntity<FixedAssetParameterChange> createParameterChange(
            @RequestBody FixedAssetParameterChange change) {
        FixedAssetParameterChange created = parameterChangeService.createParameterChange(change);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/{changeId}/post")
    @Operation(summary = "Post parameter change", description = "Post parameter change (apply to asset)")
    public ResponseEntity<Void> postParameterChange(@PathVariable Long changeId) {
        parameterChangeService.postParameterChange(changeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{changeId}")
    @Operation(summary = "Get parameter change by ID", description = "Retrieve a parameter change by ID")
    public ResponseEntity<FixedAssetParameterChange> getParameterChangeById(@PathVariable Long changeId) {
        FixedAssetParameterChange change = parameterChangeService.getParameterChangeById(changeId);
        return ResponseEntity.ok(change);
    }

    @GetMapping("/asset/{assetId}")
    @Operation(summary = "Get parameter changes by asset", description = "Get all parameter changes for an asset")
    public ResponseEntity<List<FixedAssetParameterChange>> getParameterChangesByAsset(@PathVariable Long assetId) {
        List<FixedAssetParameterChange> changes = parameterChangeService.getParameterChangesByAsset(assetId);
        return ResponseEntity.ok(changes);
    }

    @GetMapping("/unposted")
    @Operation(summary = "Get unposted parameter changes", description = "Get all unposted parameter changes")
    public ResponseEntity<List<FixedAssetParameterChange>> getUnpostedParameterChanges() {
        List<FixedAssetParameterChange> changes = parameterChangeService.getUnpostedParameterChanges();
        return ResponseEntity.ok(changes);
    }
}
