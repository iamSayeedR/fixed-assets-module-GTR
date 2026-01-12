package com.example.accounts.service;

import com.example.accounts.dto.FixedAssetClassRequest;
import com.example.accounts.dto.FixedAssetClassResponse;
import com.example.accounts.entity.FixedAssetClass;
import com.example.accounts.exception.ResourceNotFoundException;
import com.example.accounts.repository.FixedAssetClassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for Fixed Asset Class operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FixedAssetClassService {

    private final FixedAssetClassRepository fixedAssetClassRepository;

    /**
     * Create a new fixed asset class
     */
    public FixedAssetClassResponse createClass(FixedAssetClassRequest request) {
        log.info("Creating fixed asset class: {}", request.getDescription());

        FixedAssetClass assetClass = new FixedAssetClass();
        assetClass.setCode(request.getCode());
        assetClass.setDescription(request.getDescription());
        assetClass.setAssetType(
                request.getAssetType() != null ? request.getAssetType() : "Property, Plant and Equipment");
        assetClass.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        // Set parent class if provided
        if (request.getParentClassId() != null) {
            FixedAssetClass parentClass = fixedAssetClassRepository.findById(request.getParentClassId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Parent class not found with id: " + request.getParentClassId()));
            assetClass.setParentClass(parentClass);
        }

        FixedAssetClass saved = fixedAssetClassRepository.save(assetClass);
        log.info("Created fixed asset class with id: {}", saved.getClassId());

        return toResponse(saved);
    }

    /**
     * Get class by ID
     */
    @Transactional(readOnly = true)
    public FixedAssetClassResponse getClassById(Long classId) {
        FixedAssetClass assetClass = fixedAssetClassRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Fixed asset class not found with id: " + classId));
        return toResponse(assetClass);
    }

    /**
     * Get all classes
     */
    @Transactional(readOnly = true)
    public List<FixedAssetClassResponse> getAllClasses() {
        return fixedAssetClassRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all active classes
     */
    @Transactional(readOnly = true)
    public List<FixedAssetClassResponse> getActiveClasses() {
        return fixedAssetClassRepository.findByIsActiveTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get root classes (no parent)
     */
    @Transactional(readOnly = true)
    public List<FixedAssetClassResponse> getRootClasses() {
        return fixedAssetClassRepository.findRootClasses().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get child classes by parent ID
     */
    @Transactional(readOnly = true)
    public List<FixedAssetClassResponse> getChildClasses(Long parentId) {
        return fixedAssetClassRepository.findByParentClassId(parentId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update class
     */
    public FixedAssetClassResponse updateClass(Long classId, FixedAssetClassRequest request) {
        log.info("Updating fixed asset class: {}", classId);

        FixedAssetClass assetClass = fixedAssetClassRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Fixed asset class not found with id: " + classId));

        if (request.getCode() != null) {
            assetClass.setCode(request.getCode());
        }
        if (request.getDescription() != null) {
            assetClass.setDescription(request.getDescription());
        }
        if (request.getAssetType() != null) {
            assetClass.setAssetType(request.getAssetType());
        }
        if (request.getIsActive() != null) {
            assetClass.setIsActive(request.getIsActive());
        }

        // Update parent class
        if (request.getParentClassId() != null) {
            FixedAssetClass parentClass = fixedAssetClassRepository.findById(request.getParentClassId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Parent class not found with id: " + request.getParentClassId()));
            assetClass.setParentClass(parentClass);
        }

        FixedAssetClass updated = fixedAssetClassRepository.save(assetClass);
        log.info("Updated fixed asset class: {}", classId);

        return toResponse(updated);
    }

    /**
     * Delete class
     */
    public void deleteClass(Long classId) {
        log.info("Deleting fixed asset class: {}", classId);

        if (!fixedAssetClassRepository.existsById(classId)) {
            throw new ResourceNotFoundException("Fixed asset class not found with id: " + classId);
        }

        fixedAssetClassRepository.deleteById(classId);
        log.info("Deleted fixed asset class: {}", classId);
    }

    /**
     * Convert entity to response DTO
     */
    private FixedAssetClassResponse toResponse(FixedAssetClass assetClass) {
        FixedAssetClassResponse response = new FixedAssetClassResponse();
        response.setClassId(assetClass.getClassId());
        response.setCode(assetClass.getCode());
        response.setDescription(assetClass.getDescription());
        response.setAssetType(assetClass.getAssetType());
        response.setIsActive(assetClass.getIsActive());

        if (assetClass.getParentClass() != null) {
            response.setParentClassId(assetClass.getParentClass().getClassId());
            response.setParentClassName(assetClass.getParentClass().getDescription());
        }

        return response;
    }
}
