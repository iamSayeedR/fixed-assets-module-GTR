package com.example.accounts.repository;

import com.example.accounts.entity.FixedAssetMonthlyUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Fixed Asset Monthly Usage
 */
@Repository
public interface FixedAssetMonthlyUsageRepository extends JpaRepository<FixedAssetMonthlyUsage, Long> {

    /**
     * Find by usage number
     */
    Optional<FixedAssetMonthlyUsage> findByUsageNumber(String usageNumber);

    /**
     * Find by fixed asset
     */
    @Query("SELECT famu FROM FixedAssetMonthlyUsage famu WHERE famu.fixedAsset.fixedAssetId = :assetId ORDER BY famu.usagePeriod DESC")
    List<FixedAssetMonthlyUsage> findByFixedAssetId(@Param("assetId") Long assetId);

    /**
     * Find by asset and period
     */
    @Query("SELECT famu FROM FixedAssetMonthlyUsage famu WHERE famu.fixedAsset.fixedAssetId = :assetId AND famu.usagePeriod = :period")
    Optional<FixedAssetMonthlyUsage> findByFixedAssetIdAndPeriod(
            @Param("assetId") Long assetId,
            @Param("period") LocalDate period);

    /**
     * Find by period
     */
    List<FixedAssetMonthlyUsage> findByUsagePeriod(LocalDate period);

    /**
     * Find processed usage
     */
    List<FixedAssetMonthlyUsage> findByIsProcessedTrue();

    /**
     * Find unprocessed usage
     */
    List<FixedAssetMonthlyUsage> findByIsProcessedFalse();
}
