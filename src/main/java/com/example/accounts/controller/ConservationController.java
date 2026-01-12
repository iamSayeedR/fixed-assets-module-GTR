package com.example.accounts.controller;

import com.example.accounts.dto.ConservationRequest;
import com.example.accounts.dto.ConservationResponse;
import com.example.accounts.entity.FixedAsset;
import com.example.accounts.entity.FixedAssetConservation;
import com.example.accounts.repository.FixedAssetRepository;
import com.example.accounts.service.ConservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for Fixed Asset Conservation operations
 */
@RestController
@RequestMapping("/api/conservations")
@RequiredArgsConstructor
@Tag(name = "Fixed Asset Conservations", description = "Manage asset conservations (pause/resume depreciation)")
public class ConservationController {

    private final ConservationService conservationService;
    private final FixedAssetRepository fixedAssetRepository;

    @PostMapping("/start")
    @Operation(summary = "Start conservation", description = "Place an asset in conservation and pause depreciation")
    public ResponseEntity<ConservationResponse> startConservation(@RequestBody ConservationRequest request) {
        FixedAsset asset = fixedAssetRepository.findById(request.getFixedAssetId())
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        FixedAssetConservation conservation = new FixedAssetConservation();
        conservation.setFixedAsset(asset);
        conservation.setConservationNumber(request.getConservationNumber());
        conservation.setConservationDate(request.getConservationDate());
        conservation.setEntity(request.getEntity());
        conservation.setReason(request.getReason());
        conservation.setResponsible(request.getResponsible());
        conservation.setDepreciationSuspensionDate(request.getDepreciationSuspensionDate());
        conservation.setPlannedEndDate(request.getPlannedEndDate());
        conservation.setComment(request.getComment());
        conservation.setTransactionRemarks(request.getTransactionRemarks());

        FixedAssetConservation saved = conservationService.startConservation(conservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel conservation", description = "Cancel conservation and resume depreciation")
    public ResponseEntity<ConservationResponse> cancelConservation(
            @PathVariable Long id,
            @RequestBody ConservationRequest request) {
        FixedAssetConservation cancelled = conservationService.cancelConservation(id, request.getCancellationReason());
        return ResponseEntity.ok(toResponse(cancelled));
    }

    @PostMapping("/{id}/post")
    @Operation(summary = "Post conservation", description = "Post conservation document")
    public ResponseEntity<Void> postConservation(@PathVariable Long id) {
        conservationService.postConservation(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get conservation by ID", description = "Retrieve conservation details by ID")
    public ResponseEntity<ConservationResponse> getById(@PathVariable Long id) {
        FixedAssetConservation conservation = conservationService.getById(id);
        return ResponseEntity.ok(toResponse(conservation));
    }

    @GetMapping("/asset/{assetId}")
    @Operation(summary = "Get conservations by asset", description = "Get all conservations for a specific asset")
    public ResponseEntity<List<ConservationResponse>> getByAssetId(@PathVariable Long assetId) {
        List<FixedAssetConservation> conservations = conservationService.getByAssetId(assetId);
        List<ConservationResponse> responses = conservations.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/asset/{assetId}/active")
    @Operation(summary = "Get active conservation", description = "Get active conservation for an asset")
    public ResponseEntity<ConservationResponse> getActiveByAssetId(@PathVariable Long assetId) {
        return conservationService.getActiveConservationByAssetId(assetId)
                .map(c -> ResponseEntity.ok(toResponse(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/unposted")
    @Operation(summary = "Get unposted conservations", description = "Get all unposted conservation documents")
    public ResponseEntity<List<ConservationResponse>> getUnposted() {
        List<FixedAssetConservation> conservations = conservationService.getUnposted();
        List<ConservationResponse> responses = conservations.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/active")
    @Operation(summary = "Get active conservations", description = "Get all active (not cancelled) conservations")
    public ResponseEntity<List<ConservationResponse>> getActiveConservations() {
        List<FixedAssetConservation> conservations = conservationService.getActiveConservations();
        List<ConservationResponse> responses = conservations.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping
    @Operation(summary = "Get all conservations", description = "Get all conservation documents")
    public ResponseEntity<List<ConservationResponse>> getAll() {
        List<FixedAssetConservation> conservations = conservationService.getAll();
        List<ConservationResponse> responses = conservations.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Convert entity to response DTO
     */
    private ConservationResponse toResponse(FixedAssetConservation conservation) {
        ConservationResponse response = new ConservationResponse();
        response.setConservationId(conservation.getConservationId());
        response.setConservationNumber(conservation.getConservationNumber());
        response.setConservationDate(conservation.getConservationDate());
        response.setTransactionType(conservation.getTransactionType());

        // Asset details
        if (conservation.getFixedAsset() != null) {
            response.setFixedAssetId(conservation.getFixedAsset().getFixedAssetId());
            response.setAssetNumber(conservation.getFixedAsset().getAssetNumber());
            response.setAssetDescription(conservation.getFixedAsset().getDescription());
        }

        // Conservation details
        response.setEntity(conservation.getEntity());
        response.setReason(conservation.getReason());
        response.setResponsible(conservation.getResponsible());
        response.setDepreciationSuspensionDate(conservation.getDepreciationSuspensionDate());
        response.setPlannedEndDate(conservation.getPlannedEndDate());

        // Financial snapshot
        response.setGrossCostAtConservation(conservation.getGrossCostAtConservation());
        response.setSalvageValueAtConservation(conservation.getSalvageValueAtConservation());
        response.setAccumulatedDepreciationAtConservation(conservation.getAccumulatedDepreciationAtConservation());
        response.setNetBookValueAtConservation(conservation.getNetBookValueAtConservation());
        response.setUsefulLifeMonthsAtConservation(conservation.getUsefulLifeMonthsAtConservation());
        response.setDepreciationMethodAtConservation(conservation.getDepreciationMethodAtConservation());

        // Cancellation
        response.setIsCancelled(conservation.getIsCancelled());
        response.setCancellationDate(conservation.getCancellationDate());
        response.setCancellationNumber(conservation.getCancellationNumber());
        response.setCancellationReason(conservation.getCancellationReason());

        // Posting
        response.setIsPosted(conservation.getIsPosted());
        response.setPostedDate(conservation.getPostedDate());

        // Comments
        response.setComment(conservation.getComment());
        response.setTransactionRemarks(conservation.getTransactionRemarks());

        return response;
    }
}
