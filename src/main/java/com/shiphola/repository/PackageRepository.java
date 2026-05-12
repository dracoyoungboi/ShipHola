package com.shiphola.repository;

import com.shiphola.entity.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Package Repository - Quản lý gói hàng logistics
 * Shared repository cho cả AdminService và DispatcherService
 */
@Repository
public interface PackageRepository extends JpaRepository<Package, Long> {

    // Find packages by status
    List<Package> findByStatus(String status);

    // Find packages by dispatcher
    @Query("SELECT p FROM Package p WHERE p.dispatcher.userId = :dispatcherId AND p.deleted = false ORDER BY p.createdAt DESC")
    List<Package> findByDispatcher(@Param("dispatcherId") Long dispatcherId);

    // Find packages by shipper
    @Query("SELECT p FROM Package p WHERE p.shipper.userId = :shipperId AND p.deleted = false ORDER BY p.createdAt DESC")
    List<Package> findByShipper(@Param("shipperId") Long shipperId);

    // Count packages by status
    long countByStatus(String status);

    // Count pending packages
    @Query("SELECT COUNT(p) FROM Package p WHERE p.status = 'PENDING' AND p.deleted = false")
    long countPending();

    // Count assigned packages
    @Query("SELECT COUNT(p) FROM Package p WHERE p.status = 'ASSIGNED' AND p.deleted = false")
    long countAssigned();

    // Count in transit packages
    @Query("SELECT COUNT(p) FROM Package p WHERE p.status = 'IN_TRANSIT' AND p.deleted = false")
    long countInTransit();

    // Count delivered today
    @Query("SELECT COUNT(p) FROM Package p WHERE p.status = 'DELIVERED' AND DATE(p.deliveredAt) = CURDATE() AND p.deleted = false")
    long countDeliveredToday();

    // Find packages by delivery area
    @Query("SELECT p FROM Package p WHERE p.deliveryArea = :area AND p.deleted = false ORDER BY p.createdAt DESC")
    List<Package> findByDeliveryArea(@Param("area") String area);

    // Search packages by tracking number or phone
    @Query("SELECT p FROM Package p WHERE p.deleted = false AND " +
           "LOWER(p.trackingNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.senderPhone) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.receiverPhone) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "ORDER BY p.createdAt DESC")
    List<Package> searchPackages(@Param("keyword") String keyword);
}
