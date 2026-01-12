package com.example.accounts.repository;

import com.example.accounts.entity.FixedAssetParameterChange;
import com.example.accounts.entity.enums.ParameterChangeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Fixed Asset Parameter Changes
 */
@Repository
public interface FixedAssetParameterChangeRepository extends JpaRepository<FixedAssetParameterChange, Long> {

    /**
     * Find by change number
     */
    Optional<FixedAssetParameterChange> findByChangeNumber(String changeNumber);

    /**
     * Find by fixed asset
     */
    @Query("SELECT fapc FROM FixedAssetParameterChange fapc WHERE fapc.fixedAsset.fixedAssetId = :assetId ORDER BY fapc.changeDate DESC")
    List<FixedAssetParameterChange> findByFixedAssetId(@Param("assetId") Long assetId);

    /**
     * Find by change type
     */
    List<FixedAssetParameterChange> findByChangeType(ParameterChangeType changeType);

    /**
     * Find by change date range
     */
    List<FixedAssetParameterChange> findByChangeDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Find posted changes
     */
    List<FixedAssetParameterChange> findByIsPostedTrue();

    /**
     * Find unposted changes
     */
    List<FixedAssetParameterChange> findByIsPostedFalse();
}
