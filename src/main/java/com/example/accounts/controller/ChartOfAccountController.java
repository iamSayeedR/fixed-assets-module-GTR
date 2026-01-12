package com.example.accounts.controller;

import com.example.accounts.dto.ChartOfAccountRequest;
import com.example.accounts.dto.ChartOfAccountResponse;
import com.example.accounts.dto.ChartOfAccountTreeNode;
import com.example.accounts.service.ChartOfAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chart-of-accounts")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@Tag(name = "Chart of Accounts", description = "APIs for managing General Ledger Chart of Accounts")
public class ChartOfAccountController {

    private final ChartOfAccountService chartOfAccountService;

    @PostMapping
    @Operation(summary = "Create a new GL account")
    public ResponseEntity<ChartOfAccountResponse> createAccount(@Valid @RequestBody ChartOfAccountRequest request) {
        ChartOfAccountResponse response = chartOfAccountService.createAccount(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{accountId}")
    @Operation(summary = "Get GL account by ID")
    public ResponseEntity<ChartOfAccountResponse> getAccountById(@PathVariable Long accountId) {
        ChartOfAccountResponse response = chartOfAccountService.getAccountById(accountId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{accountCode}")
    @Operation(summary = "Get GL account by account code")
    public ResponseEntity<ChartOfAccountResponse> getAccountByCode(@PathVariable String accountCode) {
        ChartOfAccountResponse response = chartOfAccountService.getAccountByCode(accountCode);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/section/{section}")
    @Operation(summary = "Get all GL accounts by section")
    public ResponseEntity<List<ChartOfAccountResponse>> getAccountsBySection(@PathVariable String section) {
        List<ChartOfAccountResponse> responses = chartOfAccountService.getAccountsBySection(section);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/parent-group/{parentGroup}")
    @Operation(summary = "Get all GL accounts by parent group")
    public ResponseEntity<List<ChartOfAccountResponse>> getAccountsByParentGroup(@PathVariable String parentGroup) {
        List<ChartOfAccountResponse> responses = chartOfAccountService.getAccountsByParentGroup(parentGroup);
        return ResponseEntity.ok(responses);
    }

    @GetMapping
    @Operation(summary = "Get all GL accounts")
    public ResponseEntity<List<ChartOfAccountResponse>> getAllAccounts() {
        List<ChartOfAccountResponse> responses = chartOfAccountService.getAllAccounts();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active GL accounts")
    public ResponseEntity<List<ChartOfAccountResponse>> getActiveAccounts() {
        List<ChartOfAccountResponse> responses = chartOfAccountService.getActiveAccounts();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{accountId}")
    @Operation(summary = "Update GL account")
    public ResponseEntity<ChartOfAccountResponse> updateAccount(
            @PathVariable Long accountId,
            @Valid @RequestBody ChartOfAccountRequest request) {
        ChartOfAccountResponse response = chartOfAccountService.updateAccount(accountId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{accountId}/activate")
    @Operation(summary = "Activate GL account")
    public ResponseEntity<ChartOfAccountResponse> activateAccount(@PathVariable Long accountId) {
        ChartOfAccountResponse response = chartOfAccountService.activateAccount(accountId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{accountId}/deactivate")
    @Operation(summary = "Deactivate GL account")
    public ResponseEntity<ChartOfAccountResponse> deactivateAccount(@PathVariable Long accountId) {
        ChartOfAccountResponse response = chartOfAccountService.deactivateAccount(accountId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tree")
    @Operation(summary = "Get chart of accounts as hierarchical tree structure with optional filters")
    public ResponseEntity<List<ChartOfAccountTreeNode>> getAccountsTree(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String section) {
        List<ChartOfAccountTreeNode> tree = chartOfAccountService.getAccountsTree(code, description, type, section);
        return ResponseEntity.ok(tree);
    }

    @DeleteMapping("/{accountId}")
    @Operation(summary = "Delete GL account")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long accountId) {
        chartOfAccountService.deleteAccount(accountId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/dimension/{dimensionType}")
    @Operation(summary = "Get accounts that can be used as dimension values", description = "Returns all active accounts that have the specified dimension type. "
            +
            "For example: /dimension/WAREHOUSE returns all accounts with WAREHOUSE dimension, " +
            "which can be used to populate warehouse dropdown in journal entries.")
    public ResponseEntity<List<ChartOfAccountResponse>> getAccountsForDimension(@PathVariable String dimensionType) {
        List<ChartOfAccountResponse> responses = chartOfAccountService.getAccountsForDimension(dimensionType);
        return ResponseEntity.ok(responses);
    }
}
