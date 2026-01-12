package com.example.accounts.controller;

import com.example.accounts.dto.FixedAssetClassRequest;
import com.example.accounts.dto.FixedAssetClassResponse;
import com.example.accounts.service.FixedAssetClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Fixed Asset Classes
 */
@RestController
@RequestMapping("/api/fixed-asset-classes")
@RequiredArgsConstructor
@Tag(name = "Fixed Asset Classes", description = "Manage fixed asset classes and categories")
public class FixedAssetClassController {

    private final FixedAssetClassService fixedAssetClassService;

    @PostMapping
    @Operation(summary = "Create fixed asset class", description = "Create a new fixed asset class/category")
    public ResponseEntity<FixedAssetClassResponse> createClass(@RequestBody FixedAssetClassRequest request) {
        FixedAssetClassResponse response = fixedAssetClassService.createClass(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{classId}")
    @Operation(summary = "Get class by ID", description = "Retrieve a fixed asset class by its ID")
    public ResponseEntity<FixedAssetClassResponse> getClassById(@PathVariable Long classId) {
        FixedAssetClassResponse response = fixedAssetClassService.getClassById(classId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all classes", description = "Retrieve all fixed asset classes")
    public ResponseEntity<List<FixedAssetClassResponse>> getAllClasses() {
        List<FixedAssetClassResponse> response = fixedAssetClassService.getAllClasses();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    @Operation(summary = "Get active classes", description = "Retrieve all active fixed asset classes")
    public ResponseEntity<List<FixedAssetClassResponse>> getActiveClasses() {
        List<FixedAssetClassResponse> response = fixedAssetClassService.getActiveClasses();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/root")
    @Operation(summary = "Get root classes", description = "Retrieve root classes (classes without parent)")
    public ResponseEntity<List<FixedAssetClassResponse>> getRootClasses() {
        List<FixedAssetClassResponse> response = fixedAssetClassService.getRootClasses();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/parent/{parentId}/children")
    @Operation(summary = "Get child classes", description = "Retrieve child classes by parent ID")
    public ResponseEntity<List<FixedAssetClassResponse>> getChildClasses(@PathVariable Long parentId) {
        List<FixedAssetClassResponse> response = fixedAssetClassService.getChildClasses(parentId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{classId}")
    @Operation(summary = "Update class", description = "Update an existing fixed asset class")
    public ResponseEntity<FixedAssetClassResponse> updateClass(
            @PathVariable Long classId,
            @RequestBody FixedAssetClassRequest request) {
        FixedAssetClassResponse response = fixedAssetClassService.updateClass(classId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{classId}")
    @Operation(summary = "Delete class", description = "Delete a fixed asset class")
    public ResponseEntity<Void> deleteClass(@PathVariable Long classId) {
        fixedAssetClassService.deleteClass(classId);
        return ResponseEntity.noContent().build();
    }
}
