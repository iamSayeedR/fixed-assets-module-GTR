package com.example.accounts.repository;

import com.example.accounts.entity.FixedAssetCapitalImprovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Fixed Asset Capital Improvements
 */
@Repository
public interface FixedAssetCapitalImprovementRepository extends JpaRepository<FixedAssetCapitalImprovement, Long> {

    /**
     * Find by improvement number
     */
    Optional<FixedAssetCapitalImprovement> findByImprovementNumber(String improvementNumber);

    /**
     * Find by fixed asset
     */
    @Query("SELECT faci FROM FixedAssetCapitalImprovement faci WHERE faci.fixedAsset.fixedAssetId = :assetId ORDER BY faci.improvementDate DESC")
    List<FixedAssetCapitalImprovement> findByFixedAssetId(@Param("assetId") Long assetId);

    /**
     * Find by improvement date range
     */
    List<FixedAssetCapitalImprovement> findByImprovementDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Find posted improvements
     */
    List<FixedAssetCapitalImprovement> findByIsPostedTrue();

    /**
     * Find unposted improvements
     */
    List<FixedAssetCapitalImprovement> findByIsPostedFalse();
}
