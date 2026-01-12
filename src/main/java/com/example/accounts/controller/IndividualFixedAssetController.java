package com.example.accounts.controller;

import com.example.accounts.dto.IndividualFixedAssetRequest;
import com.example.accounts.dto.IndividualFixedAssetResponse;
import com.example.accounts.entity.FixedAsset;
import com.example.accounts.entity.IndividualFixedAsset;
import com.example.accounts.repository.FixedAssetRepository;
import com.example.accounts.service.IndividualFixedAssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for Individual Fixed Assets (Employee Assignments)
 */
@RestController
@RequestMapping("/api/individual-fixed-assets")
@RequiredArgsConstructor
@Tag(name = "Individual Fixed Assets", description = "Manage fixed assets assigned to employees")
public class IndividualFixedAssetController {

    private final IndividualFixedAssetService assignmentService;
    private final FixedAssetRepository fixedAssetRepository;

    @PostMapping("/transfer")
    @Operation(summary = "Transfer asset to employee", description = "Assign a fixed asset to an employee")
    public ResponseEntity<IndividualFixedAssetResponse> transferToEmployee(
            @RequestBody IndividualFixedAssetRequest request) {
        FixedAsset asset = fixedAssetRepository.findById(request.getFixedAssetId())
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        IndividualFixedAsset assignment = new IndividualFixedAsset();
        assignment.setFixedAsset(asset);
        assignment.setAssignmentNumber(request.getAssignmentNumber());
        assignment.setAssignmentDate(request.getAssignmentDate());
        assignment.setEmployeeId(request.getEmployeeId());
        assignment.setEmployeeName(request.getEmployeeName());
        assignment.setEmployeeDepartment(request.getEmployeeDepartment());
        assignment.setEmployeePosition(request.getEmployeePosition());
        assignment.setEmployeeEmail(request.getEmployeeEmail());
        assignment.setPurpose(request.getPurpose());
        assignment.setExpectedReturnDate(request.getExpectedReturnDate());
        assignment.setConditionAtAssignment(request.getConditionAtAssignment());
        assignment.setResponsibilityAgreementSigned(request.getResponsibilityAgreementSigned());
        assignment.setAgreementDate(request.getAgreementDate());
        assignment.setWitnessName(request.getWitnessName());
        assignment.setNotes(request.getNotes());

        IndividualFixedAsset saved = assignmentService.transferToEmployee(assignment);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @PostMapping("/{id}/return")
    @Operation(summary = "Return asset from employee", description = "Process return of asset from employee")
    public ResponseEntity<IndividualFixedAssetResponse> returnFromEmployee(
            @PathVariable Long id,
            @RequestBody IndividualFixedAssetRequest request) {
        IndividualFixedAsset returned = assignmentService.returnFromEmployee(
                id,
                request.getActualReturnDate(),
                request.getConditionAtReturn(),
                request.getReturnNotes(),
                request.getDamageReported(),
                request.getDamageDescription());
        return ResponseEntity.ok(toResponse(returned));
    }

    @PostMapping("/{id}/post")
    @Operation(summary = "Post assignment", description = "Post assignment document")
    public ResponseEntity<Void> postAssignment(@PathVariable Long id) {
        assignmentService.postAssignment(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get assignment by ID", description = "Retrieve assignment details by ID")
    public ResponseEntity<IndividualFixedAssetResponse> getById(@PathVariable Long id) {
        IndividualFixedAsset assignment = assignmentService.getById(id);
        return ResponseEntity.ok(toResponse(assignment));
    }

    @GetMapping("/asset/{assetId}")
    @Operation(summary = "Get assignments by asset", description = "Get all assignments for a specific asset")
    public ResponseEntity<List<IndividualFixedAssetResponse>> getByAssetId(@PathVariable Long assetId) {
        List<IndividualFixedAsset> assignments = assignmentService.getByAssetId(assetId);
        List<IndividualFixedAssetResponse> responses = assignments.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/asset/{assetId}/current")
    @Operation(summary = "Get current assignment", description = "Get current assignment for an asset")
    public ResponseEntity<IndividualFixedAssetResponse> getCurrentAssignmentByAssetId(@PathVariable Long assetId) {
        return assignmentService.getCurrentAssignmentByAssetId(assetId)
                .map(a -> ResponseEntity.ok(toResponse(a)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get assignments by employee", description = "Get all assignments for a specific employee")
    public ResponseEntity<List<IndividualFixedAssetResponse>> getByEmployeeId(@PathVariable String employeeId) {
        List<IndividualFixedAsset> assignments = assignmentService.getByEmployeeId(employeeId);
        List<IndividualFixedAssetResponse> responses = assignments.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/employee/{employeeId}/current")
    @Operation(summary = "Get current assignments by employee", description = "Get current assignments for an employee")
    public ResponseEntity<List<IndividualFixedAssetResponse>> getCurrentAssignmentsByEmployeeId(
            @PathVariable String employeeId) {
        List<IndividualFixedAsset> assignments = assignmentService.getCurrentAssignmentsByEmployeeId(employeeId);
        List<IndividualFixedAssetResponse> responses = assignments.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/unposted")
    @Operation(summary = "Get unposted assignments", description = "Get all unposted assignment documents")
    public ResponseEntity<List<IndividualFixedAssetResponse>> getUnposted() {
        List<IndividualFixedAsset> assignments = assignmentService.getUnposted();
        List<IndividualFixedAssetResponse> responses = assignments.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get assignments by status", description = "Get assignments by status (ASSIGNED, RETURNED, etc.)")
    public ResponseEntity<List<IndividualFixedAssetResponse>> getByStatus(@PathVariable String status) {
        List<IndividualFixedAsset> assignments = assignmentService.getByStatus(status);
        List<IndividualFixedAssetResponse> responses = assignments.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue returns", description = "Get assignments with overdue return dates")
    public ResponseEntity<List<IndividualFixedAssetResponse>> getOverdueReturns() {
        List<IndividualFixedAsset> assignments = assignmentService.getOverdueReturns();
        List<IndividualFixedAssetResponse> responses = assignments.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping
    @Operation(summary = "Get all assignments", description = "Get all assignment documents")
    public ResponseEntity<List<IndividualFixedAssetResponse>> getAll() {
        List<IndividualFixedAsset> assignments = assignmentService.getAll();
        List<IndividualFixedAssetResponse> responses = assignments.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Convert entity to response DTO
     */
    private IndividualFixedAssetResponse toResponse(IndividualFixedAsset assignment) {
        IndividualFixedAssetResponse response = new IndividualFixedAssetResponse();
        response.setAssignmentId(assignment.getAssignmentId());
        response.setAssignmentNumber(assignment.getAssignmentNumber());
        response.setAssignmentDate(assignment.getAssignmentDate());
        response.setTransactionType(assignment.getTransactionType());

        // Asset details
        if (assignment.getFixedAsset() != null) {
            response.setFixedAssetId(assignment.getFixedAsset().getFixedAssetId());
            response.setAssetNumber(assignment.getFixedAsset().getAssetNumber());
            response.setAssetDescription(assignment.getFixedAsset().getDescription());
        }

        // Employee details
        response.setEmployeeId(assignment.getEmployeeId());
        response.setEmployeeName(assignment.getEmployeeName());
        response.setEmployeeDepartment(assignment.getEmployeeDepartment());
        response.setEmployeePosition(assignment.getEmployeePosition());
        response.setEmployeeEmail(assignment.getEmployeeEmail());

        // Assignment details
        response.setPurpose(assignment.getPurpose());
        response.setExpectedReturnDate(assignment.getExpectedReturnDate());
        response.setActualReturnDate(assignment.getActualReturnDate());
        response.setStatus(assignment.getStatus());
        response.setConditionAtAssignment(assignment.getConditionAtAssignment());
        response.setConditionAtReturn(assignment.getConditionAtReturn());

        // Responsibility
        response.setResponsibilityAgreementSigned(assignment.getResponsibilityAgreementSigned());
        response.setAgreementDate(assignment.getAgreementDate());
        response.setWitnessName(assignment.getWitnessName());

        // Return details
        response.setReturnNotes(assignment.getReturnNotes());
        response.setDamageReported(assignment.getDamageReported());
        response.setDamageDescription(assignment.getDamageDescription());

        // Posting
        response.setIsPosted(assignment.getIsPosted());
        response.setPostedDate(assignment.getPostedDate());

        response.setNotes(assignment.getNotes());

        return response;
    }
}
