package com.example.accounts.repository;

import com.example.accounts.entity.FixedAssetSalePreparation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Fixed Asset Sale Preparations
 */
@Repository
public interface FixedAssetSalePreparationRepository extends JpaRepository<FixedAssetSalePreparation, Long> {

    /**
     * Find by preparation number
     */
    Optional<FixedAssetSalePreparation> findByPreparationNumber(String preparationNumber);

    /**
     * Find by fixed asset
     */
    @Query("SELECT fasp FROM FixedAssetSalePreparation fasp WHERE fasp.fixedAsset.fixedAssetId = :assetId ORDER BY fasp.preparationDate DESC")
    List<FixedAssetSalePreparation> findByFixedAssetId(@Param("assetId") Long assetId);

    /**
     * Find by preparation date range
     */
    List<FixedAssetSalePreparation> findByPreparationDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Find posted preparations
     */
    List<FixedAssetSalePreparation> findByIsPostedTrue();

    /**
     * Find unposted preparations
     */
    List<FixedAssetSalePreparation> findByIsPostedFalse();

    /**
     * Find preparations without actual sale
     */
    @Query("SELECT fasp FROM FixedAssetSalePreparation fasp WHERE fasp.actualSale IS NULL")
    List<FixedAssetSalePreparation> findPendingSales();
}
