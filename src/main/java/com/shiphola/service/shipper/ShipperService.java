package com.shiphola.service.shipper;

import com.shiphola.dto.request.UpdatePackageStatusDTO;
import com.shiphola.entity.Package;

import java.util.List;

public interface ShipperService {

    List<Package> getMyPackages(Long shipperId);

    List<Package> getAvailablePackages(Long shipperId);

    Package acceptPackage(Long packageId, Long shipperId);

    Package updatePackageStatus(Long packageId, UpdatePackageStatusDTO dto, Long shipperId);

    Package getPackageById(Long packageId);

    long getPendingDeliveryCount(Long shipperId);

    long getDeliveredTodayCount(Long shipperId);
}
