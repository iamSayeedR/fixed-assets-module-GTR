package com.example.accounts.controller;

import com.example.accounts.entity.FixedAssetWriteOff;
import com.example.accounts.service.WriteOffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Write-Offs
 */
@RestController
@RequestMapping("/api/write-offs")
@RequiredArgsConstructor
@Tag(name = "Write-Offs", description = "Manage fixed asset write-offs")
public class WriteOffController {

    private final WriteOffService writeOffService;

    @PostMapping
    @Operation(summary = "Create write-off", description = "Create a fixed asset write-off")
    public ResponseEntity<FixedAssetWriteOff> createWriteOff(@RequestBody FixedAssetWriteOff writeOff) {
        FixedAssetWriteOff created = writeOffService.createWriteOff(writeOff);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/{writeOffId}/post")
    @Operation(summary = "Post write-off", description = "Post write-off (change asset status to WRITTEN_OFF)")
    public ResponseEntity<Void> postWriteOff(@PathVariable Long writeOffId) {
        writeOffService.postWriteOff(writeOffId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{writeOffId}")
    @Operation(summary = "Get write-off by ID", description = "Retrieve a write-off by ID")
    public ResponseEntity<FixedAssetWriteOff> getWriteOffById(@PathVariable Long writeOffId) {
        FixedAssetWriteOff writeOff = writeOffService.getWriteOffById(writeOffId);
        return ResponseEntity.ok(writeOff);
    }

    @GetMapping("/asset/{assetId}")
    @Operation(summary = "Get write-offs by asset", description = "Get all write-offs for an asset")
    public ResponseEntity<List<FixedAssetWriteOff>> getWriteOffsByAsset(@PathVariable Long assetId) {
        List<FixedAssetWriteOff> writeOffs = writeOffService.getWriteOffsByAsset(assetId);
        return ResponseEntity.ok(writeOffs);
    }

    @GetMapping("/unposted")
    @Operation(summary = "Get unposted write-offs", description = "Get all unposted write-offs")
    public ResponseEntity<List<FixedAssetWriteOff>> getUnpostedWriteOffs() {
        List<FixedAssetWriteOff> writeOffs = writeOffService.getUnpostedWriteOffs();
        return ResponseEntity.ok(writeOffs);
    }
}
