package com.shiphola.controller.dispatcher;

import com.shiphola.dto.request.AssignPackageDTO;
import com.shiphola.entity.Package;
import com.shiphola.entity.User;
import com.shiphola.service.dispatcher.DispatcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/dispatcher")
@PreAuthorize("hasAnyRole('DISPATCHER')")
public class DispatcherController {

    @Autowired
    private DispatcherService dispatcherService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pendingCount", dispatcherService.getPendingCount());
        model.addAttribute("assignedCount", dispatcherService.getAssignedCount());
        return "dispatcher/dashboard";
    }

    @GetMapping("/packages/pending")
    public String pendingPackages(Model model) {
        List<Package> packages = dispatcherService.getPendingPackages();
        model.addAttribute("packages", packages);
        model.addAttribute("title", "Đơn hàng chờ xử lý");
        return "dispatcher/packages";
    }

    @GetMapping("/packages/assigned")
    public String assignedPackages(Model model) {
        Long dispatcherId = 1L;
        List<Package> packages = dispatcherService.getAssignedPackages(dispatcherId);
        model.addAttribute("packages", packages);
        model.addAttribute("title", "Đơn hàng đã giao");
        return "dispatcher/packages";
    }

    @GetMapping("/packages/{id}")
    public String viewPackage(@PathVariable Long id, Model model) {
        Package pkg = dispatcherService.getPackageById(id);
        if (pkg == null) {
            return "redirect:/dispatcher/packages/pending";
        }

        List<User> shippers = dispatcherService.getAvailableShippers();
        model.addAttribute("package", pkg);
        model.addAttribute("shippers", shippers);
        model.addAttribute("assignDTO", new AssignPackageDTO());
        return "dispatcher/package-detail";
    }

    @PostMapping("/packages/{id}/assign")
    public String assignPackage(@PathVariable Long id,
                               @ModelAttribute AssignPackageDTO dto,
                               RedirectAttributes redirectAttributes) {
        try {
            Long dispatcherId = 1L;
            dispatcherService.assignPackageToShipper(id, dto, dispatcherId);
            redirectAttributes.addFlashAttribute("success", "Đã giao đơn hàng cho shipper");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/dispatcher/packages/pending";
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String keyword, Model model) {
        if (keyword != null && !keyword.isEmpty()) {
            List<Package> packages = dispatcherService.searchPackages(keyword);
            model.addAttribute("packages", packages);
            model.addAttribute("keyword", keyword);
        }
        return "dispatcher/search";
    }
}
