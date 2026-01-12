package com.example.accounts.service;

import com.example.accounts.entity.FixedAsset;
import com.example.accounts.entity.IndividualFixedAsset;
import com.example.accounts.exception.BusinessException;
import com.example.accounts.exception.ResourceNotFoundException;
import com.example.accounts.repository.FixedAssetRepository;
import com.example.accounts.repository.IndividualFixedAssetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for Individual Fixed Assets (Employee Assignments)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class IndividualFixedAssetService {

    private final IndividualFixedAssetRepository assignmentRepository;
    private final FixedAssetRepository fixedAssetRepository;

    /**
     * Transfer asset to employee
     */
    public IndividualFixedAsset transferToEmployee(IndividualFixedAsset assignment) {
        log.info("Transferring asset {} to employee {}",
                assignment.getFixedAsset().getFixedAssetId(),
                assignment.getEmployeeId());

        FixedAsset asset = assignment.getFixedAsset();

        // Check if asset is already assigned
        Optional<IndividualFixedAsset> currentAssignment = assignmentRepository
                .findCurrentAssignmentByAssetId(asset.getFixedAssetId());

        if (currentAssignment.isPresent()) {
            throw new BusinessException("Asset is already assigned to employee: " +
                    currentAssignment.get().getEmployeeName());
        }

        // Set transaction type and status
        assignment.setTransactionType("Transfer to employee");
        assignment.setStatus("ASSIGNED");

        // Save assignment
        IndividualFixedAsset saved = assignmentRepository.save(assignment);

        log.info("Created assignment {} for asset {} to employee {}",
                saved.getAssignmentId(),
                asset.getFixedAssetId(),
                assignment.getEmployeeId());

        return saved;
    }

    /**
     * Return asset from employee (creates new "Transfer from employee" transaction)
     */
    public IndividualFixedAsset returnFromEmployee(Long assignmentId, LocalDate returnDate,
            String conditionAtReturn, String returnNotes,
            Boolean damageReported, String damageDescription) {
        log.info("Processing return for assignment: {}", assignmentId);

        IndividualFixedAsset originalAssignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + assignmentId));

        if (originalAssignment.getActualReturnDate() != null) {
            throw new BusinessException(
                    "Asset has already been returned on: " + originalAssignment.getActualReturnDate());
        }

        // Update original assignment with return date
        originalAssignment.setActualReturnDate(returnDate);
        originalAssignment.setStatus("RETURNED");
        assignmentRepository.save(originalAssignment);

        // Create new "Transfer from employee" transaction
        IndividualFixedAsset returnTransaction = new IndividualFixedAsset();
        returnTransaction.setFixedAsset(originalAssignment.getFixedAsset());
        returnTransaction.setAssignmentNumber(generateReturnNumber(originalAssignment.getAssignmentNumber()));
        returnTransaction.setAssignmentDate(returnDate);
        returnTransaction.setTransactionType("Transfer from employee");
        returnTransaction.setEmployeeId(originalAssignment.getEmployeeId());
        returnTransaction.setEmployeeName(originalAssignment.getEmployeeName());
        returnTransaction.setEmployeeDepartment(originalAssignment.getEmployeeDepartment());
        returnTransaction.setEmployeePosition(originalAssignment.getEmployeePosition());
        returnTransaction.setEmployeeEmail(originalAssignment.getEmployeeEmail());
        returnTransaction.setActualReturnDate(returnDate);
        returnTransaction.setConditionAtReturn(conditionAtReturn);
        returnTransaction.setReturnNotes(returnNotes);
        returnTransaction.setDamageReported(damageReported != null ? damageReported : false);
        returnTransaction.setDamageDescription(damageDescription);
        returnTransaction.setStatus("RETURNED");

        IndividualFixedAsset saved = assignmentRepository.save(returnTransaction);

        log.info("Created return transaction {} for assignment {}", saved.getAssignmentId(), assignmentId);

        return saved;
    }

    /**
     * Generate return transaction number
     */
    private String generateReturnNumber(String originalNumber) {
        return originalNumber + "-RET";
    }

    /**
     * Post assignment
     */
    public void postAssignment(Long assignmentId) {
        log.info("Posting assignment: {}", assignmentId);

        IndividualFixedAsset assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + assignmentId));

        if (assignment.getIsPosted()) {
            throw new BusinessException("Assignment is already posted");
        }

        // Mark as posted
        assignment.setIsPosted(true);
        assignment.setPostedDate(LocalDateTime.now());
        assignmentRepository.save(assignment);

        log.info("Posted assignment: {}", assignmentId);
    }

    /**
     * Get assignment by ID
     */
    @Transactional(readOnly = true)
    public IndividualFixedAsset getById(Long id) {
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + id));
    }

    /**
     * Get all assignments for an asset
     */
    @Transactional(readOnly = true)
    public List<IndividualFixedAsset> getByAssetId(Long assetId) {
        return assignmentRepository.findByFixedAssetId(assetId);
    }

    /**
     * Get current assignment for an asset
     */
    @Transactional(readOnly = true)
    public Optional<IndividualFixedAsset> getCurrentAssignmentByAssetId(Long assetId) {
        return assignmentRepository.findCurrentAssignmentByAssetId(assetId);
    }

    /**
     * Get all assignments for an employee
     */
    @Transactional(readOnly = true)
    public List<IndividualFixedAsset> getByEmployeeId(String employeeId) {
        return assignmentRepository.findByEmployeeId(employeeId);
    }

    /**
     * Get current assignments for an employee
     */
    @Transactional(readOnly = true)
    public List<IndividualFixedAsset> getCurrentAssignmentsByEmployeeId(String employeeId) {
        return assignmentRepository.findCurrentAssignmentsByEmployeeId(employeeId);
    }

    /**
     * Get unposted assignments
     */
    @Transactional(readOnly = true)
    public List<IndividualFixedAsset> getUnposted() {
        return assignmentRepository.findUnposted();
    }

    /**
     * Get assignments by status
     */
    @Transactional(readOnly = true)
    public List<IndividualFixedAsset> getByStatus(String status) {
        return assignmentRepository.findByStatus(status);
    }

    /**
     * Get overdue returns
     */
    @Transactional(readOnly = true)
    public List<IndividualFixedAsset> getOverdueReturns() {
        return assignmentRepository.findOverdueReturns(LocalDate.now());
    }

    /**
     * Get all assignments
     */
    @Transactional(readOnly = true)
    public List<IndividualFixedAsset> getAll() {
        return assignmentRepository.findAll();
    }
}
