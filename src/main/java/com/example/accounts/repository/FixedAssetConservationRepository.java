package com.example.accounts.repository;

import com.example.accounts.entity.FixedAssetConservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Fixed Asset Conservation operations
 */
@Repository
public interface FixedAssetConservationRepository extends JpaRepository<FixedAssetConservation, Long> {

    /**
     * Find conservation by number
     */
    Optional<FixedAssetConservation> findByConservationNumber(String conservationNumber);

    /**
     * Find all conservations for a specific asset
     */
    @Query("SELECT c FROM FixedAssetConservation c WHERE c.fixedAsset.fixedAssetId = :assetId ORDER BY c.conservationDate DESC")
    List<FixedAssetConservation> findByFixedAssetId(@Param("assetId") Long assetId);

    /**
     * Find active (not cancelled) conservation for an asset
     */
    @Query("SELECT c FROM FixedAssetConservation c WHERE c.fixedAsset.fixedAssetId = :assetId AND c.isCancelled = false ORDER BY c.conservationDate DESC")
    Optional<FixedAssetConservation> findActiveConservationByAssetId(@Param("assetId") Long assetId);

    /**
     * Find all unposted conservations
     */
    @Query("SELECT c FROM FixedAssetConservation c WHERE c.isPosted = false ORDER BY c.conservationDate DESC")
    List<FixedAssetConservation> findUnposted();

    /**
     * Find conservations by transaction type
     */
    @Query("SELECT c FROM FixedAssetConservation c WHERE c.transactionType = :transactionType ORDER BY c.conservationDate DESC")
    List<FixedAssetConservation> findByTransactionType(@Param("transactionType") String transactionType);

    /**
     * Find conservations by date range
     */
    @Query("SELECT c FROM FixedAssetConservation c WHERE c.conservationDate BETWEEN :startDate AND :endDate ORDER BY c.conservationDate DESC")
    List<FixedAssetConservation> findByDateRange(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Find all active (not cancelled) conservations
     */
    @Query("SELECT c FROM FixedAssetConservation c WHERE c.isCancelled = false ORDER BY c.conservationDate DESC")
    List<FixedAssetConservation> findActiveConservations();

    /**
     * Find conservations by entity
     */
    @Query("SELECT c FROM FixedAssetConservation c WHERE c.entity = :entity ORDER BY c.conservationDate DESC")
    List<FixedAssetConservation> findByEntity(@Param("entity") String entity);
}
