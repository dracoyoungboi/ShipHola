package com.shiphola.service.impl;

import com.shiphola.constant.OrderStatus;
import com.shiphola.dto.request.AssignPackageDTO;
import com.shiphola.entity.Package;
import com.shiphola.entity.PackageHistory;
import com.shiphola.entity.User;
import com.shiphola.repository.PackageHistoryRepository;
import com.shiphola.repository.PackageRepository;
import com.shiphola.repository.UserRepository;
import com.shiphola.service.dispatcher.DispatcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DispatcherServiceImpl implements DispatcherService {

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private PackageHistoryRepository historyRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Package> getPendingPackages() {
        return packageRepository.findByStatus("PENDING").stream()
                .filter(pkg -> !pkg.getDeleted())
                .toList();
    }

    @Override
    public List<Package> getAssignedPackages(Long dispatcherId) {
        return packageRepository.findByDispatcher(dispatcherId);
    }

    @Override
    @Transactional
    public Package assignPackageToShipper(Long packageId, AssignPackageDTO dto, Long dispatcherId) {
        Package pkg = packageRepository.findById(packageId).orElse(null);
        if (pkg == null || pkg.getDeleted()) {
            throw new RuntimeException("Không tìm thấy gói hàng");
        }

        if (!pkg.canBeAssigned()) {
            throw new RuntimeException("Gói hàng không thể giao cho shipper");
        }

        User shipper = userRepository.findById(dto.getShipperId()).orElse(null);
        if (shipper == null) {
            throw new RuntimeException("Không tìm thấy shipper");
        }

        User dispatcher = userRepository.findById(dispatcherId).orElse(null);

        pkg.setDispatcher(dispatcher);
        pkg.setShipper(shipper);
        pkg.setStatus("ASSIGNED");

        Package pkgSaved = packageRepository.save(pkg);

        PackageHistory history = new PackageHistory(
                pkg,
                OrderStatus.PENDING,
                OrderStatus.ASSIGNED,
                dto.getNote(),
                dispatcher != null ? dispatcher.getFullName() : "System"
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
    public List<Package> searchPackages(String keyword) {
        return packageRepository.searchPackages(keyword);
    }

    @Override
    public List<User> getAvailableShippers() {
        return userRepository.findByRoleAndNotDeleted(com.shiphola.constant.Role.SHIPPER);
    }

    @Override
    public long getPendingCount() {
        return packageRepository.countPending();
    }

    @Override
    public long getAssignedCount() {
        return packageRepository.countAssigned();
    }
}
