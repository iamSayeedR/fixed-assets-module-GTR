package com.example.accounts.repository;

import com.example.accounts.entity.FixedAssetWriteOff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Fixed Asset Write-Offs
 */
@Repository
public interface FixedAssetWriteOffRepository extends JpaRepository<FixedAssetWriteOff, Long> {

    /**
     * Find by write-off number
     */
    Optional<FixedAssetWriteOff> findByWriteOffNumber(String writeOffNumber);

    /**
     * Find by fixed asset
     */
    @Query("SELECT fawo FROM FixedAssetWriteOff fawo WHERE fawo.fixedAsset.fixedAssetId = :assetId")
    List<FixedAssetWriteOff> findByFixedAssetId(@Param("assetId") Long assetId);

    /**
     * Find by write-off date range
     */
    List<FixedAssetWriteOff> findByWriteOffDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Find posted write-offs
     */
    List<FixedAssetWriteOff> findByIsPostedTrue();

    /**
     * Find unposted write-offs
     */
    List<FixedAssetWriteOff> findByIsPostedFalse();
}
