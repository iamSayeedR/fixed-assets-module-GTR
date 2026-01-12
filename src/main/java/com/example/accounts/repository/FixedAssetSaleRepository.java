package com.example.accounts.repository;

import com.example.accounts.entity.FixedAssetSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Fixed Asset Sales
 */
@Repository
public interface FixedAssetSaleRepository extends JpaRepository<FixedAssetSale, Long> {

    /**
     * Find by sale number
     */
    Optional<FixedAssetSale> findBySaleNumber(String saleNumber);

    /**
     * Find by fixed asset
     */
    @Query("SELECT fas FROM FixedAssetSale fas WHERE fas.fixedAsset.fixedAssetId = :assetId")
    List<FixedAssetSale> findByFixedAssetId(@Param("assetId") Long assetId);

    /**
     * Find by buyer
     */
    @Query("SELECT fas FROM FixedAssetSale fas WHERE fas.buyer.companyId = :buyerId")
    List<FixedAssetSale> findByBuyerId(@Param("buyerId") Long buyerId);

    /**
     * Find by sale date range
     */
    List<FixedAssetSale> findBySaleDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Find posted sales
     */
    List<FixedAssetSale> findByIsPostedTrue();

    /**
     * Find unposted sales
     */
    List<FixedAssetSale> findByIsPostedFalse();
}
