package com.shiphola.controller.shipper;

import com.shiphola.dto.request.UpdatePackageStatusDTO;
import com.shiphola.entity.Package;
import com.shiphola.service.shipper.ShipperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/shipper")
@PreAuthorize("hasAnyRole('SHIPPER')")
public class ShipperController {

    @Autowired
    private ShipperService shipperService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Long shipperId = 1L;
        model.addAttribute("pendingCount", shipperService.getPendingDeliveryCount(shipperId));
        model.addAttribute("deliveredCount", shipperService.getDeliveredTodayCount(shipperId));
        return "shipper/dashboard";
    }

    @GetMapping("/packages")
    public String myPackages(Model model) {
        Long shipperId = 1L;
        List<Package> packages = shipperService.getMyPackages(shipperId);
        model.addAttribute("packages", packages);
        return "shipper/packages";
    }

    @GetMapping("/packages/available")
    public String availablePackages(Model model) {
        Long shipperId = 1L;
        List<Package> packages = shipperService.getAvailablePackages(shipperId);
        model.addAttribute("packages", packages);
        return "shipper/available-packages";
    }

    @GetMapping("/packages/{id}")
    public String viewPackage(@PathVariable Long id, Model model) {
        Package pkg = shipperService.getPackageById(id);
        if (pkg == null) {
            return "redirect:/shipper/packages";
        }

        model.addAttribute("package", pkg);
        model.addAttribute("statusDTO", new UpdatePackageStatusDTO());
        return "shipper/package-detail";
    }

    @PostMapping("/packages/{id}/accept")
    public String acceptPackage(@PathVariable Long id,
                               RedirectAttributes redirectAttributes) {
        try {
            Long shipperId = 1L;
            shipperService.acceptPackage(id, shipperId);
            redirectAttributes.addFlashAttribute("success", "Đã nhận đơn hàng thành công");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/shipper/packages";
    }

    @PostMapping("/packages/{id}/update-status")
    public String updateStatus(@PathVariable Long id,
                              @ModelAttribute UpdatePackageStatusDTO dto,
                              RedirectAttributes redirectAttributes) {
        try {
            Long shipperId = 1L;
            shipperService.updatePackageStatus(id, dto, shipperId);
            redirectAttributes.addFlashAttribute("success", "Đã cập nhật trạng thái");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/shipper/packages/" + id;
    }

    @GetMapping("/map")
    public String mapView() {
        return "shipper/map";
    }
}
