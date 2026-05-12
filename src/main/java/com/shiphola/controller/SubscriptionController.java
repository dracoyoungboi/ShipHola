package com.shiphola.controller;

import com.shiphola.constant.SubscriptionStatus;
import com.shiphola.dto.request.SubscriptionDTO;
import com.shiphola.entity.Subscription;
import com.shiphola.entity.User;
import com.shiphola.service.subscription.SubscriptionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/subscriptions")
@PreAuthorize("hasAnyRole('ADMIN')")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @GetMapping
    public String listSubscriptions(Model model) {
        List<Subscription> subscriptions = subscriptionService.getAllSubscriptions();
        model.addAttribute("subscriptions", subscriptions);
        return "admin/subscriptions";
    }

    @GetMapping("/create")
    public String createSubscriptionForm(Model model) {
        model.addAttribute("subscriptionDTO", new SubscriptionDTO());
        model.addAttribute("plans", com.shiphola.constant.SubscriptionPlan.values());
        return "admin/subscription-form";
    }

    @PostMapping("/create")
    public String createSubscription(@Valid SubscriptionDTO dto,
                                    BindingResult result,
                                    RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/subscription-form";
        }

        try {
            subscriptionService.createSubscription(dto);
            redirectAttributes.addFlashAttribute("success", "Đã tạo subscription thành công");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/subscriptions";
    }

    @GetMapping("/{id}")
    public String viewSubscription(@PathVariable Long id, Model model) {
        Subscription subscription = subscriptionService.getSubscriptionById(id);
        if (subscription == null) {
            return "redirect:/admin/subscriptions";
        }

        List<User> users = subscriptionService.getUsersBySubscription(id);
        model.addAttribute("subscription", subscription);
        model.addAttribute("users", users);
        return "admin/subscription-detail";
    }

    @PostMapping("/{id}/update")
    public String updateSubscription(@PathVariable Long id,
                                    @Valid SubscriptionDTO dto,
                                    BindingResult result,
                                    RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/subscription-form";
        }

        try {
            subscriptionService.updateSubscription(id, dto);
            redirectAttributes.addFlashAttribute("success", "Đã cập nhật subscription thành công");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/subscriptions/" + id;
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                              @RequestParam SubscriptionStatus status,
                              RedirectAttributes redirectAttributes) {
        subscriptionService.updateSubscriptionStatus(id, status);
        redirectAttributes.addFlashAttribute("success", "Đã cập nhật trạng thái");
        return "redirect:/admin/subscriptions/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteSubscription(@PathVariable Long id,
                                    RedirectAttributes redirectAttributes) {
        subscriptionService.deleteSubscription(id);
        redirectAttributes.addFlashAttribute("success", "Đã xóa subscription thành công");
        return "redirect:/admin/subscriptions";
    }
}
