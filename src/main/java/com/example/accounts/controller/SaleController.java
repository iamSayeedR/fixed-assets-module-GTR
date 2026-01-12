package com.example.accounts.controller;

import com.example.accounts.entity.FixedAssetSale;
import com.example.accounts.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Sales (Disposal)
 */
@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
@Tag(name = "Sales", description = "Manage fixed asset sales and disposals")
public class SaleController {

    private final SaleService saleService;

    @PostMapping
    @Operation(summary = "Create sale", description = "Create a fixed asset sale (disposal)")
    public ResponseEntity<FixedAssetSale> createSale(@RequestBody FixedAssetSale sale) {
        FixedAssetSale created = saleService.createSale(sale);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/{saleId}/post")
    @Operation(summary = "Post sale", description = "Post sale (change status to DISPOSED)")
    public ResponseEntity<Void> postSale(@PathVariable Long saleId) {
        saleService.postSale(saleId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{saleId}")
    @Operation(summary = "Get sale by ID", description = "Retrieve a sale by ID")
    public ResponseEntity<FixedAssetSale> getSaleById(@PathVariable Long saleId) {
        FixedAssetSale sale = saleService.getSaleById(saleId);
        return ResponseEntity.ok(sale);
    }

    @GetMapping("/asset/{assetId}")
    @Operation(summary = "Get sales by asset", description = "Get all sales for an asset")
    public ResponseEntity<List<FixedAssetSale>> getSalesByAsset(@PathVariable Long assetId) {
        List<FixedAssetSale> sales = saleService.getSalesByAsset(assetId);
        return ResponseEntity.ok(sales);
    }

    @GetMapping("/buyer/{buyerId}")
    @Operation(summary = "Get sales by buyer", description = "Get all sales for a specific buyer")
    public ResponseEntity<List<FixedAssetSale>> getSalesByBuyer(@PathVariable Long buyerId) {
        List<FixedAssetSale> sales = saleService.getSalesByBuyer(buyerId);
        return ResponseEntity.ok(sales);
    }

    @GetMapping("/unposted")
    @Operation(summary = "Get unposted sales", description = "Get all unposted sales")
    public ResponseEntity<List<FixedAssetSale>> getUnpostedSales() {
        List<FixedAssetSale> sales = saleService.getUnpostedSales();
        return ResponseEntity.ok(sales);
    }
}
