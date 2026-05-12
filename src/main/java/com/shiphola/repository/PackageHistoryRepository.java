package com.shiphola.repository;

import com.shiphola.entity.Package;
import com.shiphola.entity.PackageHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackageHistoryRepository extends JpaRepository<PackageHistory, Long> {

    List<PackageHistory> findByPackageEntity_PackageIdOrderByCreatedAtDesc(Long packageId);

    @Query("SELECT ph FROM PackageHistory ph WHERE ph.packageEntity.packageId = :packageId ORDER BY ph.createdAt DESC")
    List<PackageHistory> findHistoryByPackageId(@Param("packageId") Long packageId);

    @Query("SELECT ph FROM PackageHistory ph WHERE ph.updatedBy = :userId ORDER BY ph.createdAt DESC")
    List<PackageHistory> findByUpdatedBy(@Param("userId") String userId);

    @Query("SELECT COUNT(ph) FROM PackageHistory ph WHERE ph.packageEntity.packageId = :packageId")
    long countByPackageId(@Param("packageId") Long packageId);
}
