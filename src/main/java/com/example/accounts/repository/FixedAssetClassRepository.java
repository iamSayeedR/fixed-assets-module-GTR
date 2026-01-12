package com.example.accounts.repository;

import com.example.accounts.entity.FixedAssetClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Fixed Asset Classes
 */
@Repository
public interface FixedAssetClassRepository extends JpaRepository<FixedAssetClass, Long> {

    /**
     * Find by code
     */
    Optional<FixedAssetClass> findByCode(String code);

    /**
     * Find all active classes
     */
    List<FixedAssetClass> findByIsActiveTrue();

    /**
     * Find by description containing (case-insensitive)
     */
    List<FixedAssetClass> findByDescriptionContainingIgnoreCase(String description);

    /**
     * Find by asset type
     */
    List<FixedAssetClass> findByAssetType(String assetType);

    /**
     * Find root classes (no parent)
     */
    @Query("SELECT fac FROM FixedAssetClass fac WHERE fac.parentClass IS NULL")
    List<FixedAssetClass> findRootClasses();

    /**
     * Find child classes by parent
     */
    @Query("SELECT fac FROM FixedAssetClass fac WHERE fac.parentClass.classId = :parentId")
    List<FixedAssetClass> findByParentClassId(@Param("parentId") Long parentId);
}
