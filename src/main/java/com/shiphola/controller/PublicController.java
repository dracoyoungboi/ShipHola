package com.shiphola.controller;

import com.shiphola.dto.request.CalculateFeeDTO;
import com.shiphola.dto.request.ConfirmOrderDTO;
import com.shiphola.dto.request.PublicOrderDTO;
import com.shiphola.dto.response.FeeCalculationResponse;
import com.shiphola.dto.response.OrderConfirmationResponse;
import com.shiphola.entity.Package;
import com.shiphola.service.MapService;
import com.shiphola.service.landing.LandingPageService;
import com.shiphola.util.RateLimitUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * PublicController - Controller cho Landing Page (Guest/Customer)
 * Xử lý navigation và business logic cho trang public
 *
 * Theo CLAUDE.md: Role Guest (Public) -> PublicController -> LandingPageService -> templates/public/
 */
@Controller
public class PublicController {

    @Autowired
    private LandingPageService landingPageService;

    @Autowired
    private MapService mapService;

    @Autowired
    private RateLimitUtil rateLimitUtil;

    @Value("${ship.hola.hotline:0909 xxx xxx}")
    private String hotline;

    /**
     * Trang chủ (root) - Landing page
     */
    @GetMapping({"/", "/home"})
    public String home() {
        return "redirect:/public/landing";
    }

    /**
     * Landing page - Trang chủ public
     */
    @GetMapping({"/public/landing", "/public"})
    public String landing(Model model) {
        model.addAttribute("publicOrderDTO", new PublicOrderDTO());
        return "public/landing";
    }

    /**
     * Add form attribute to all requests
     */
    @ModelAttribute
    public void addAttributes(Model model) {
        if (!model.containsAttribute("publicOrderDTO")) {
            model.addAttribute("publicOrderDTO", new PublicOrderDTO());
        }
    }

    /**
     * Xử lý đặt đơn từ landing page
     */
    @PostMapping("/public/order")
    public String createOrder(@Valid @ModelAttribute PublicOrderDTO dto,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            // Collect errors and add to flash attributes
            StringBuilder errors = new StringBuilder("Vui lòng kiểm tra lại: ");
            result.getFieldErrors().forEach(error -> {
                errors.append(error.getDefaultMessage()).append(". ");
            });
            redirectAttributes.addFlashAttribute("error", errors.toString());
            return "redirect:/public/landing#order";
        }

        try {
            Package pkg = landingPageService.createPublicOrder(dto);
            redirectAttributes.addFlashAttribute("success",
                "Đơn hàng đã được tạo thành công! Mã tracking: " + pkg.getTrackingNumber());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/public/landing";
    }

    /**
     * Tra cứu đơn hàng
     */
    @GetMapping("/public/search")
    public String searchOrder(@RequestParam String keyword, Model model) {
        List<Package> packages = landingPageService.searchOrder(keyword);
        model.addAttribute("packages", packages);
        model.addAttribute("keyword", keyword);
        return "public/search-result";
    }

    /**
     * Tra cứu đơn hàng (POST từ query bar)
     */
    @PostMapping("/public/search")
    public String searchOrderPost(@RequestParam String keyword) {
        return "redirect:/public/search?keyword=" + keyword;
    }

    // ==========================================
    // AJAX ENDPOINTS (Step 1 & Step 2)
    // ==========================================

    /**
     * AJAX Step 1: Tính phí ship
     * GET /public/calculate-fee?pickupAddress=...&deliveryAddress=...
     */
    @GetMapping("/public/calculate-fee")
    @ResponseBody
    public FeeCalculationResponse calculateFee(@Valid CalculateFeeDTO dto) {
        FeeCalculationResponse response;

        switch (dto.getServiceType()) {
            case "BUY_FOR":
            case "BUS_PICKUP":
                // Fixed fee
                response = new FeeCalculationResponse();
                response.setDistance(0.0);
                response.setDuration(30);
                response.setFee(20000.0);
                response.setSurcharge(dto.getSurcharge());
                break;

            case "DRIVER_HELP":
                // Contact for quote
                response = new FeeCalculationResponse();
                response.setFee(null); // Negotiable
                response.setSurcharge(dto.getSurcharge());
                break;

            case "DELIVERY":
            default:
                // Calculate from addresses
                response = mapService.calculateFeeFromAddresses(
                        dto.getPickupAddress(),
                        dto.getDeliveryAddress()
                );
                if (response != null) {
                    response.setSurcharge(dto.getSurcharge());
                } else {
                    // Fallback if geocoding fails
                    response = new FeeCalculationResponse();
                    response.setFee(null); // Negotiable
                }
                break;
        }

        return response;
    }

    /**
     * AJAX: Reverse geocoding - Lấy địa chỉ từ tọa độ
     * GET /public/reverse-geocode?lat=...&lon=...
     */
    @GetMapping("/public/reverse-geocode")
    @ResponseBody
    public String reverseGeocode(@RequestParam double lat, @RequestParam double lon) {
        String address = mapService.reverseGeocode(lat, lon);
        if (address == null) {
            // Return coordinates as fallback
            return String.format("Vị trí: %.6f, %.6f (Không thể xác định địa chỉ)", lat, lon);
        }
        return address;
    }

    /**
     * AJAX Step 2: Xác nhận đặt đơn
     * POST /public/confirm-order
     */
    @PostMapping("/public/confirm-order")
    @ResponseBody
    public OrderConfirmationResponse confirmOrder(
            @Valid @RequestBody ConfirmOrderDTO dto,
            HttpServletRequest request) {

        // Get client IP
        String clientIp = getClientIp(request);

        // Check rate limit
        if (!rateLimitUtil.isAllowed(clientIp)) {
            int remainingMinutes = rateLimitUtil.getRemainingMinutes(clientIp);
            return OrderConfirmationResponse.rateLimitError(remainingMinutes);
        }

        try {
            // Save order
            Package pkg = landingPageService.confirmOrder(dto);
            return OrderConfirmationResponse.success(pkg.getTrackingNumber(), hotline);
        } catch (Exception e) {
            return OrderConfirmationResponse.error("Có lỗi xảy ra: " + e.getMessage());
        }
    }

    /**
     * Lấy IP address của client
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // For localhost testing
        if (ip == null || ip.isEmpty() || "0:0:0:0:0:0:0:1".equals(ip) || "127.0.0.1".equals(ip)) {
            ip = "localhost";
        }
        return ip;
    }
}
