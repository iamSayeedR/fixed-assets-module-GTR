package com.example.accounts.controller;

import com.example.accounts.entity.FixedAssetEntry;
import com.example.accounts.service.FixedAssetEntryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Fixed Asset Entries (Asset Activation)
 */
@RestController
@RequestMapping("/api/fixed-asset-entries")
@RequiredArgsConstructor
@Tag(name = "Fixed Asset Entries", description = "Activate fixed assets and set depreciation parameters")
public class FixedAssetEntryController {

    private final FixedAssetEntryService entryService;

    @PostMapping
    @Operation(summary = "Create and post entry", description = "Create and post a fixed asset entry (activates the asset)")
    public ResponseEntity<FixedAssetEntry> createAndPostEntry(@RequestBody FixedAssetEntry entry) {
        FixedAssetEntry created = entryService.createAndPostEntry(entry);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/{entryId}/post")
    @Operation(summary = "Post entry", description = "Post an existing entry (activate asset)")
    public ResponseEntity<Void> postEntry(@PathVariable Long entryId) {
        entryService.postEntry(entryId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{entryId}")
    @Operation(summary = "Get entry by ID", description = "Retrieve a fixed asset entry by ID")
    public ResponseEntity<FixedAssetEntry> getEntryById(@PathVariable Long entryId) {
        FixedAssetEntry entry = entryService.getEntryById(entryId);
        return ResponseEntity.ok(entry);
    }

    @GetMapping("/asset/{assetId}")
    @Operation(summary = "Get entries by asset", description = "Retrieve all entries for a specific asset")
    public ResponseEntity<List<FixedAssetEntry>> getEntriesByAsset(@PathVariable Long assetId) {
        List<FixedAssetEntry> entries = entryService.getEntriesByAsset(assetId);
        return ResponseEntity.ok(entries);
    }
}
