package com.shiphola.service.impl;

import com.shiphola.constant.OrderStatus;
import com.shiphola.dto.request.UpdatePackageStatusDTO;
import com.shiphola.entity.Package;
import com.shiphola.entity.PackageHistory;
import com.shiphola.repository.PackageHistoryRepository;
import com.shiphola.repository.PackageRepository;
import com.shiphola.service.shipper.ShipperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShipperServiceImpl implements ShipperService {

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private PackageHistoryRepository historyRepository;

    @Override
    public List<Package> getMyPackages(Long shipperId) {
        return packageRepository.findByShipper(shipperId);
    }

    @Override
    public List<Package> getAvailablePackages(Long shipperId) {
        return packageRepository.findByStatus("ASSIGNED").stream()
                .filter(pkg -> pkg.getShipper() != null &&
                        pkg.getShipper().getUserId().equals(shipperId))
                .toList();
    }

    @Override
    @Transactional
    public Package acceptPackage(Long packageId, Long shipperId) {
        Package pkg = packageRepository.findById(packageId).orElse(null);
        if (pkg == null) {
            throw new RuntimeException("Không tìm thấy gói hàng");
        }

        if (!"ASSIGNED".equals(pkg.getStatus())) {
            throw new RuntimeException("Gói hàng không ở trạng thái chờ nhận");
        }

        pkg.setStatus("PICKED_UP");

        PackageHistory history = new PackageHistory(
                pkg,
                OrderStatus.ASSIGNED,
                OrderStatus.PICKED_UP,
                "Shipper đã nhận hàng",
                "Shipper"
        );
        historyRepository.save(history);

        return packageRepository.save(pkg);
    }

    @Override
    @Transactional
    public Package updatePackageStatus(Long packageId, UpdatePackageStatusDTO dto, Long shipperId) {
        Package pkg = packageRepository.findById(packageId).orElse(null);
        if (pkg == null) {
            throw new RuntimeException("Không tìm thấy gói hàng");
        }

        if (!pkg.canShipperUpdate()) {
            throw new RuntimeException("Không thể cập nhật gói hàng này");
        }

        String oldStatus = pkg.getStatus();
        pkg.setStatus(dto.getStatus());

        if ("DELIVERED".equals(dto.getStatus())) {
            pkg.setDeliveredAt(LocalDateTime.now());
        }

        Package pkgSaved = packageRepository.save(pkg);

        PackageHistory history = new PackageHistory(
                pkg,
                OrderStatus.valueOf(oldStatus),
                OrderStatus.valueOf(dto.getStatus()),
                dto.getNote(),
                "Shipper"
        );
        historyRepository.save(history);

        return pkgSaved;
    }

    @Override
    public Package getPackageById(Long packageId) {
        return packageRepository.findById(packageId)
                .filter(pkg -> !pkg.getDeleted())
                .orElse(null);
    }

    @Override
    public long getPendingDeliveryCount(Long shipperId) {
        return packageRepository.findByShipper(shipperId).stream()
                .filter(pkg -> !"DELIVERED".equals(pkg.getStatus()) &&
                        !"CANCELLED".equals(pkg.getStatus()))
                .count();
    }

    @Override
    public long getDeliveredTodayCount(Long shipperId) {
        return packageRepository.findByShipper(shipperId).stream()
                .filter(pkg -> "DELIVERED".equals(pkg.getStatus()) &&
                        pkg.getDeliveredAt() != null &&
                        pkg.getDeliveredAt().toLocalDate().equals(LocalDateTime.now().toLocalDate()))
                .count();
    }
}
