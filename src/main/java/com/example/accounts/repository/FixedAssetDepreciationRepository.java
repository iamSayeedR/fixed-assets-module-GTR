package com.example.accounts.repository;

import com.example.accounts.entity.FixedAssetDepreciation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Fixed Asset Depreciation
 */
@Repository
public interface FixedAssetDepreciationRepository extends JpaRepository<FixedAssetDepreciation, Long> {

    /**
     * Find by depreciation number
     */
    Optional<FixedAssetDepreciation> findByDepreciationNumber(String depreciationNumber);

    /**
     * Find by fixed asset
     */
    @Query("SELECT fad FROM FixedAssetDepreciation fad WHERE fad.fixedAsset.fixedAssetId = :assetId ORDER BY fad.depreciationPeriod DESC")
    List<FixedAssetDepreciation> findByFixedAssetId(@Param("assetId") Long assetId);

    /**
     * Find by asset and period
     */
    @Query("SELECT fad FROM FixedAssetDepreciation fad WHERE fad.fixedAsset.fixedAssetId = :assetId AND fad.depreciationPeriod = :period")
    Optional<FixedAssetDepreciation> findByFixedAssetIdAndPeriod(
            @Param("assetId") Long assetId,
            @Param("period") LocalDate period);

    /**
     * Find by period
     */
    List<FixedAssetDepreciation> findByDepreciationPeriod(LocalDate period);

    /**
     * Find by period range
     */
    List<FixedAssetDepreciation> findByDepreciationPeriodBetween(LocalDate startPeriod, LocalDate endPeriod);

    /**
     * Find posted depreciation
     */
    List<FixedAssetDepreciation> findByIsPostedTrue();

    /**
     * Find unposted depreciation
     */
    List<FixedAssetDepreciation> findByIsPostedFalse();

    /**
     * Get latest depreciation for an asset
     */
    @Query("SELECT fad FROM FixedAssetDepreciation fad WHERE fad.fixedAsset.fixedAssetId = :assetId ORDER BY fad.depreciationPeriod DESC LIMIT 1")
    Optional<FixedAssetDepreciation> findLatestByFixedAssetId(@Param("assetId") Long assetId);
}
