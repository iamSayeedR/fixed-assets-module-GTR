package com.example.accounts.controller;

import com.example.accounts.entity.FixedAssetSalePreparation;
import com.example.accounts.service.SalePreparationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Sale Preparations (Held for Sale)
 */
@RestController
@RequestMapping("/api/sale-preparations")
@RequiredArgsConstructor
@Tag(name = "Sale Preparations", description = "Prepare fixed assets for sale (held for sale)")
public class SalePreparationController {

    private final SalePreparationService salePreparationService;

    @PostMapping
    @Operation(summary = "Create sale preparation", description = "Create a sale preparation (reclassify as held for sale)")
    public ResponseEntity<FixedAssetSalePreparation> createSalePreparation(
            @RequestBody FixedAssetSalePreparation preparation) {
        FixedAssetSalePreparation created = salePreparationService.createSalePreparation(preparation);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/{preparationId}/post")
    @Operation(summary = "Post sale preparation", description = "Post sale preparation (change status to HELD_FOR_SALE)")
    public ResponseEntity<Void> postSalePreparation(@PathVariable Long preparationId) {
        salePreparationService.postSalePreparation(preparationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{preparationId}/cancel")
    @Operation(summary = "Cancel sale preparation", description = "Cancel sale preparation (revert to ACTIVE)")
    public ResponseEntity<Void> cancelSalePreparation(@PathVariable Long preparationId) {
        salePreparationService.cancelSalePreparation(preparationId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{preparationId}")
    @Operation(summary = "Get sale preparation by ID", description = "Retrieve a sale preparation by ID")
    public ResponseEntity<FixedAssetSalePreparation> getSalePreparationById(@PathVariable Long preparationId) {
        FixedAssetSalePreparation preparation = salePreparationService.getSalePreparationById(preparationId);
        return ResponseEntity.ok(preparation);
    }

    @GetMapping("/asset/{assetId}")
    @Operation(summary = "Get sale preparations by asset", description = "Get all sale preparations for an asset")
    public ResponseEntity<List<FixedAssetSalePreparation>> getSalePreparationsByAsset(@PathVariable Long assetId) {
        List<FixedAssetSalePreparation> preparations = salePreparationService.getSalePreparationsByAsset(assetId);
        return ResponseEntity.ok(preparations);
    }

    @GetMapping("/pending")
    @Operation(summary = "Get pending sales", description = "Get all pending sales (preparations without actual sale)")
    public ResponseEntity<List<FixedAssetSalePreparation>> getPendingSales() {
        List<FixedAssetSalePreparation> preparations = salePreparationService.getPendingSales();
        return ResponseEntity.ok(preparations);
    }
}
