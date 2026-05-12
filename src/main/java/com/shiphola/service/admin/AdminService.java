package com.shiphola.service.admin;

import com.shiphola.dto.request.CreatePackageDTO;
import com.shiphola.entity.Package;
import java.util.List;

/**
 * AdminService Interface - Quản lý Admin operations
 *
 * Business Logic:
 * - Tạo gói hàng mới
 * - Xem tất cả gói hàng
 * - Thống kê dashboard
 */
public interface AdminService {

    /**
     * Tạo gói hàng mới
     */
    Package createPackage(CreatePackageDTO dto);

    /**
     * Lấy tất cả gói hàng
     */
    List<Package> getAllPackages();

    /**
     * Lấy package theo ID
     */
    Package getPackageById(Long packageId);

    /**
     * Cập nhật package
     */
    Package updatePackage(Long packageId, CreatePackageDTO dto);

    /**
     * Xóa package (soft delete)
     */
    boolean deletePackage(Long packageId);

    /**
     * Thống kê dashboard
     */
    long getTotalPackages();
    long getPendingPackages();
    long getAssignedPackages();
    long getInTransitPackages();
    long getDeliveredTodayPackages();
}
