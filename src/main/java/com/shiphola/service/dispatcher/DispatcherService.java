package com.shiphola.service.dispatcher;

import com.shiphola.dto.request.AssignPackageDTO;
import com.shiphola.entity.Package;
import com.shiphola.entity.User;

import java.util.List;

public interface DispatcherService {

    List<Package> getPendingPackages();

    List<Package> getAssignedPackages(Long dispatcherId);

    Package assignPackageToShipper(Long packageId, AssignPackageDTO dto, Long dispatcherId);

    Package getPackageById(Long packageId);

    List<Package> searchPackages(String keyword);

    List<User> getAvailableShippers();

    long getPendingCount();

    long getAssignedCount();
}
