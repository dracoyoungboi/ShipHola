package com.shiphola.service.impl;

import com.shiphola.dto.request.CreatePackageDTO;
import com.shiphola.entity.Package;
import com.shiphola.repository.PackageRepository;
import com.shiphola.service.admin.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AdminService Implementation - Admin business logic
 *
 * Global Rules Applied:
 * - Logic KHÔNG được viết trong Controller
 * - Dùng DTO để hứng form data (validate trước khi xử lý)
 * - Manual mapping DTO → Entity (KHÔNG dùng framework magic)
 */
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private PackageRepository packageRepository;

    @Override
    @Transactional
    public Package createPackage(CreatePackageDTO dto) {
        // ✅ GOOD: Manual mapping DTO → Entity
        Package pkg = new Package();

        // Basic info
        pkg.setPackageName(dto.getPackageName());
        pkg.setTrackingNumber(generateTrackingNumber());

        // Sender info
        pkg.setSenderName(dto.getSenderName());
        pkg.setSenderPhone(dto.getSenderPhone());
        pkg.setSenderAddress(dto.getSenderAddress());

        // Receiver info
        pkg.setReceiverName(dto.getReceiverName());
        pkg.setReceiverPhone(dto.getReceiverPhone());
        pkg.setReceiverAddress(dto.getReceiverAddress());

        // Package info
        pkg.setWeight(dto.getWeight());
        pkg.setDimensions(null); // Optional field
        pkg.setPackageContent(dto.getPackageContent());
        pkg.setSpecialInstructions(dto.getSpecialInstructions());

        // Delivery info
        pkg.setDeliveryType(dto.getDeliveryType());
        pkg.setShippingFee(dto.getShippingFee());
        pkg.setDeliveryArea(dto.getDeliveryArea());

        // Calculate estimated delivery time
        pkg.setEstimatedDeliveryTime(calculateEstimatedDelivery(dto.getDeliveryType()));

        // Status
        pkg.setStatus("PENDING");

        // Save
        return packageRepository.save(pkg);
    }

    @Override
    public List<Package> getAllPackages() {
        return packageRepository.findAll().stream()
                .filter(pkg -> !pkg.getDeleted())
                .toList();
    }

    @Override
    public Package getPackageById(Long packageId) {
        return packageRepository.findById(packageId)
                .filter(pkg -> !pkg.getDeleted())
                .orElse(null);
    }

    @Override
    @Transactional
    public Package updatePackage(Long packageId, CreatePackageDTO dto) {
        Package pkg = getPackageById(packageId);
        if (pkg == null) {
            return null;
        }

        // Manual mapping DTO → Entity
        pkg.setPackageName(dto.getPackageName());
        pkg.setSenderName(dto.getSenderName());
        pkg.setSenderPhone(dto.getSenderPhone());
        pkg.setSenderAddress(dto.getSenderAddress());
        pkg.setReceiverName(dto.getReceiverName());
        pkg.setReceiverPhone(dto.getReceiverPhone());
        pkg.setReceiverAddress(dto.getReceiverAddress());
        pkg.setWeight(dto.getWeight());
        pkg.setPackageContent(dto.getPackageContent());
        pkg.setSpecialInstructions(dto.getSpecialInstructions());
        pkg.setDeliveryType(dto.getDeliveryType());
        pkg.setShippingFee(dto.getShippingFee());
        pkg.setDeliveryArea(dto.getDeliveryArea());
        pkg.setEstimatedDeliveryTime(calculateEstimatedDelivery(dto.getDeliveryType()));

        return packageRepository.save(pkg);
    }

    @Override
    @Transactional
    public boolean deletePackage(Long packageId) {
        Package pkg = getPackageById(packageId);
        if (pkg == null) {
            return false;
        }

        pkg.setDeleted(true);
        packageRepository.save(pkg);

        return true;
    }

    @Override
    public long getTotalPackages() {
        return packageRepository.count();
    }

    @Override
    public long getPendingPackages() {
        return packageRepository.countPending();
    }

    @Override
    public long getAssignedPackages() {
        return packageRepository.countAssigned();
    }

    @Override
    public long getInTransitPackages() {
        return packageRepository.countInTransit();
    }

    @Override
    public long getDeliveredTodayPackages() {
        return packageRepository.countDeliveredToday();
    }

    // ==========================================
    // HELPER METHODS
    // ==========================================

    /**
     * Tạo tracking number tự động
     * Format: SH + timestamp + 6 digits random
     */
    private String generateTrackingNumber() {
        return "SH" + System.currentTimeMillis() + randomDigits(6);
    }

    /**
     * Tạo 6 chữ số ngẫu nhiên
     */
    private String randomDigits(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((int)(Math.random() * 10));
        }
        return sb.toString();
    }

    /**
     * Tính toán thời gian giao hàng dự kiến
     */
    private LocalDateTime calculateEstimatedDelivery(String deliveryType) {
        LocalDateTime now = LocalDateTime.now();

        return switch (deliveryType) {
            case "SAME_DAY" -> now.plusHours(6);
            case "EXPRESS" -> now.plusDays(1);
            case "STANDARD" -> now.plusDays(3);
            default -> now.plusDays(3);
        };
    }
}
