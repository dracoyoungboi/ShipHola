package com.shiphola.service.impl;

import com.shiphola.constant.DeliveryArea;
import com.shiphola.constant.DeliveryType;
import com.shiphola.dto.request.ConfirmOrderDTO;
import com.shiphola.dto.request.PublicOrderDTO;
import com.shiphola.entity.Package;
import com.shiphola.repository.PackageRepository;
import com.shiphola.service.landing.LandingPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * LandingPageServiceImpl - Implementation cho LandingPageService
 * Xử lý logic nghiệp vụ cho landing page
 */
@Service
public class LandingPageServiceImpl implements LandingPageService {

    @Autowired
    private PackageRepository packageRepository;

    @Override
    @Transactional
    public Package createPublicOrder(PublicOrderDTO dto) {
        Package pkg = new Package();

        // Map service type to delivery type
        switch (dto.getServiceType()) {
            case "BUY_FOR":
            case "BUS_PICKUP":
            case "DRIVER_HELP":
                pkg.setDeliveryType(DeliveryType.SAME_DAY.name());
                break;
            case "DELIVERY":
            default:
                // Determine by distance
                if (dto.getDistance() != null && dto.getDistance() <= 3) {
                    pkg.setDeliveryType(DeliveryType.SAME_DAY.name());
                } else if (dto.getDistance() != null && dto.getDistance() <= 7) {
                    pkg.setDeliveryType(DeliveryType.EXPRESS.name());
                } else {
                    pkg.setDeliveryType(DeliveryType.STANDARD.name());
                }
                break;
        }

        // Set sender info
        pkg.setSenderPhone(dto.getSenderPhone());
        pkg.setSenderAddress(dto.getPickupAddress());
        pkg.setSenderName("Khách hàng"); // Default name for public orders

        // Set receiver info
        pkg.setReceiverPhone(dto.getReceiverPhone());
        pkg.setReceiverAddress(dto.getDeliveryAddress());
        pkg.setReceiverName("Người nhận");

        // Set package info
        pkg.setPackageName(getServiceName(dto.getServiceType()));
        pkg.setPackageContent("Đơn từ landing page");
        pkg.setSpecialInstructions(dto.getNote());

        // Set weight (default estimate)
        pkg.setWeight(1.0);

        // Calculate shipping fee
        Double baseFee = calculateBaseFee(dto.getServiceType(), dto.getDistance());
        Integer surcharge = dto.getSurcharge() != null ? dto.getSurcharge() : 0;
        pkg.setShippingFee(baseFee + surcharge);

        // Set delivery area (default - can be improved with geocoding)
        pkg.setDeliveryArea(DeliveryArea.NORTH.name());

        // Status will be set to PENDING by default in entity
        pkg.setStatus("PENDING");

        return packageRepository.save(pkg);
    }

    @Override
    public List<Package> searchOrder(String keyword) {
        return packageRepository.searchPackages(keyword);
    }

    @Override
    @Transactional
    public Package confirmOrder(ConfirmOrderDTO dto) {
        Package pkg = new Package();

        // Map service type to delivery type
        switch (dto.getServiceType()) {
            case "BUY_FOR":
            case "BUS_PICKUP":
            case "DRIVER_HELP":
                pkg.setDeliveryType(DeliveryType.SAME_DAY.name());
                break;
            case "DELIVERY":
            default:
                // Determine by distance
                if (dto.getDistance() != null && dto.getDistance() <= 3) {
                    pkg.setDeliveryType(DeliveryType.SAME_DAY.name());
                } else if (dto.getDistance() != null && dto.getDistance() <= 7) {
                    pkg.setDeliveryType(DeliveryType.EXPRESS.name());
                } else {
                    pkg.setDeliveryType(DeliveryType.STANDARD.name());
                }
                break;
        }

        // Set sender info
        pkg.setSenderPhone(dto.getSenderPhone());
        pkg.setSenderAddress(dto.getPickupAddress());
        pkg.setSenderName("Khách hàng");

        // Set receiver info
        pkg.setReceiverPhone(dto.getReceiverPhone());
        pkg.setReceiverAddress(dto.getDeliveryAddress());
        pkg.setReceiverName("Người nhận");

        // Set package info
        pkg.setPackageName(getServiceName(dto.getServiceType()));
        pkg.setPackageContent("Đơn từ landing page");
        pkg.setSpecialInstructions(dto.getNote());

        // Set weight (default estimate)
        pkg.setWeight(1.0);

        // Use the calculated fee from Step 1
        Double baseFee = dto.getCalculatedFee() != null ? dto.getCalculatedFee().doubleValue() : calculateBaseFee(dto.getServiceType(), dto.getDistance());
        Integer surcharge = dto.getSurcharge() != null ? dto.getSurcharge() : 0;
        pkg.setShippingFee(baseFee + surcharge);

        // Set delivery area (default - can be improved with geocoding)
        pkg.setDeliveryArea(DeliveryArea.NORTH.name());

        // Status will be set to PENDING by default in entity
        pkg.setStatus("PENDING");

        return packageRepository.save(pkg);
    }

    /**
     * Tính phí cơ bản dựa trên loại dịch vụ và khoảng cách
     */
    private Double calculateBaseFee(String serviceType, Double distance) {
        if ("BUY_FOR".equals(serviceType) || "BUS_PICKUP".equals(serviceType)) {
            return 20000.0;
        }

        if ("DRIVER_HELP".equals(serviceType)) {
            return 0.0; // Will be quoted later
        }

        // DELIVERY - distance based pricing
        if (distance == null || distance <= 0) {
            return 12000.0;
        }

        if (distance <= 1.5) return 12000.0;
        if (distance <= 4) return 15000.0;
        if (distance <= 5) return 18000.0;
        if (distance <= 6) return 20000.0;
        if (distance <= 7) return 25000.0;

        return 30000.0; // Over 7km - default quote
    }

    /**
     * Lấy tên dịch vụ hiển thị
     */
    private String getServiceName(String serviceType) {
        switch (serviceType) {
            case "BUY_FOR": return "Mua hộ";
            case "BUS_PICKUP": return "Gửi / nhận xe buýt";
            case "DRIVER_HELP": return "Lái xe hộ / Xe ôm";
            case "DELIVERY":
            default: return "Ship hàng nhanh";
        }
    }
}
