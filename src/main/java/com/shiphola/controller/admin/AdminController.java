package com.shiphola.controller.admin;

import com.shiphola.dto.request.CreatePackageDTO;
import com.shiphola.entity.Package;
import com.shiphola.service.admin.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;

/**
 * AdminController - Quản lý Admin operations
 *
 * Global Rules Applied:
 * - Role: Admin only
 * - Responsibility: (1) Nhận DTO từ Form → (2) Gọi AdminService → (3) Trả về Model và tên file View
 * - KHÔNG viết logic nghiệp vụ trong Controller
 * - Render HTML tại templates/admin/
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAnyRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // ==========================================
    // DASHBOARD VIEW
    // ==========================================

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Statistics
        model.addAttribute("totalPackages", adminService.getTotalPackages());
        model.addAttribute("pendingPackages", adminService.getPendingPackages());
        model.addAttribute("assignedPackages", adminService.getAssignedPackages());
        model.addAttribute("inTransitPackages", adminService.getInTransitPackages());
        model.addAttribute("deliveredTodayPackages", adminService.getDeliveredTodayPackages());

        return "admin/dashboard";
    }

    // ==========================================
    // PACKAGE MANAGEMENT VIEWS
    // ==========================================

    @GetMapping("/packages")
    public String packages(Model model) {
        List<Package> packages = adminService.getAllPackages();
        model.addAttribute("packages", packages);

        return "admin/packages";
    }

    @GetMapping("/packages/create")
    public String createPackageForm(Model model) {
        return "admin/package-form";
    }

    @GetMapping("/packages/{id}")
    public String viewPackage(@PathVariable Long id, Model model) {
        Package pkg = adminService.getPackageById(id);
        if (pkg == null) {
            return "redirect:/admin/packages";
        }

        model.addAttribute("package", pkg);
        return "admin/package-detail";
    }

    // ==========================================
    // PACKAGE ACTIONS
    // ==========================================

    /**
     * ✅ GOOD: Nhận DTO từ Form submit với validation
     * Controller CHỈ: (1) Nhận DTO → (2) Gọi Service → (3) Return view
     */
    @PostMapping("/packages/create")
    public String createPackage(
            @Valid CreatePackageDTO dto,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        // Validation errors
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                "error",
                "Dữ liệu không hợp lệ. Vui lòng kiểm tra lại."
            );
            return "redirect:/admin/packages/create";
        }

        // ✅ GOOD: Gọi Service để xử lý business logic
        Package createdPackage = adminService.createPackage(dto);

        // Success message
        redirectAttributes.addFlashAttribute(
            "success",
            "Đã tạo gói hàng thành công! Mã vận đơn: " + createdPackage.getTrackingNumber()
        );

        return "redirect:/admin/packages";
    }

    @PostMapping("/packages/{id}/update")
    public String updatePackage(
            @PathVariable Long id,
            @Valid CreatePackageDTO dto,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Dữ liệu không hợp lệ");
            return "redirect:/admin/packages/" + id;
        }

        Package updatedPackage = adminService.updatePackage(id, dto);

        if (updatedPackage == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy gói hàng");
            return "redirect:/admin/packages";
        }

        redirectAttributes.addFlashAttribute("success", "Cập nhật gói hàng thành công");
        return "redirect:/admin/packages/" + id;
    }

    @PostMapping("/packages/{id}/delete")
    public String deletePackage(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        boolean deleted = adminService.deletePackage(id);

        if (!deleted) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy gói hàng");
            return "redirect:/admin/packages";
        }

        redirectAttributes.addFlashAttribute("success", "Đã xóa gói hàng thành công");
        return "redirect:/admin/packages";
    }

    // ==========================================
    // API ENDPOINTS (nếu có AJAX calls)
    // ==========================================

    @GetMapping("/api/packages")
    @ResponseBody
    public List<Package> getPackagesAPI() {
        return adminService.getAllPackages();
    }

    @GetMapping("/api/packages/{id}")
    @ResponseBody
    public Package getPackageAPI(@PathVariable Long id) {
        return adminService.getPackageById(id);
    }

    @GetMapping("/api/stats")
    @ResponseBody
    public Object getStatsAPI() {
        return java.util.Map.of(
            "totalPackages", adminService.getTotalPackages(),
            "pendingPackages", adminService.getPendingPackages(),
            "assignedPackages", adminService.getAssignedPackages(),
            "inTransitPackages", adminService.getInTransitPackages(),
            "deliveredTodayPackages", adminService.getDeliveredTodayPackages()
        );
    }
}
