package com.example.accounts.repository;

import com.example.accounts.entity.FixedAsset;
import com.example.accounts.entity.enums.AssetStatus;
import com.example.accounts.entity.enums.DepreciationMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Fixed Assets
 */
@Repository
public interface FixedAssetRepository extends JpaRepository<FixedAsset, Long> {

    /**
     * Find by asset number
     */
    Optional<FixedAsset> findByAssetNumber(String assetNumber);

    /**
     * Find by status
     */
    List<FixedAsset> findByStatus(AssetStatus status);

    /**
     * Find by class
     */
    @Query("SELECT fa FROM FixedAsset fa WHERE fa.assetClass.classId = :classId")
    List<FixedAsset> findByClassId(@Param("classId") Long classId);

    /**
     * Find by folder
     */
    List<FixedAsset> findByFolder(String folder);

    /**
     * Find by depreciation method
     */
    List<FixedAsset> findByDepreciationMethod(DepreciationMethod method);

    /**
     * Find assets needing depreciation for a given period
     */
    @Query("SELECT fa FROM FixedAsset fa WHERE " +
            "fa.status = 'ACTIVE' AND " +
            "(fa.lastDepreciationDate IS NULL OR fa.lastDepreciationDate < :targetPeriod)")
    List<FixedAsset> findAssetsNeedingDepreciation(@Param("targetPeriod") LocalDate targetPeriod);

    /**
     * Find active assets
     */
    List<FixedAsset> findByStatusIn(List<AssetStatus> statuses);

    /**
     * Find by description containing
     */
    List<FixedAsset> findByDescriptionContainingIgnoreCase(String description);

    /**
     * Find by location
     */
    List<FixedAsset> findByLocation(String location);

    /**
     * Find by department
     */
    List<FixedAsset> findByDepartment(String department);

    /**
     * Count assets by status
     */
    Long countByStatus(AssetStatus status);

    /**
     * Find assets by acquisition date range
     */
    @Query("SELECT fa FROM FixedAsset fa WHERE fa.acquisitionDate BETWEEN :startDate AND :endDate")
    List<FixedAsset> findByAcquisitionDateBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
