package com.example.accounts.repository;

import com.example.accounts.entity.FixedAssetEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Fixed Asset Entries
 */
@Repository
public interface FixedAssetEntryRepository extends JpaRepository<FixedAssetEntry, Long> {

    /**
     * Find by entry number
     */
    Optional<FixedAssetEntry> findByEntryNumber(String entryNumber);

    /**
     * Find by fixed asset
     */
    @Query("SELECT fae FROM FixedAssetEntry fae WHERE fae.fixedAsset.fixedAssetId = :assetId")
    List<FixedAssetEntry> findByFixedAssetId(@Param("assetId") Long assetId);

    /**
     * Find posted entries
     */
    List<FixedAssetEntry> findByIsPostedTrue();

    /**
     * Find unposted entries
     */
    List<FixedAssetEntry> findByIsPostedFalse();

    /**
     * Find by entry date range
     */
    List<FixedAssetEntry> findByEntryDateBetween(LocalDate startDate, LocalDate endDate);
}
