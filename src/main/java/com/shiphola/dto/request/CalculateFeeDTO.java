package com.shiphola.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * CalculateFeeDTO - DTO cho request tính phí ship
 * Sử dụng trong Step 1: Tính giá cước (không lưu DB)
 */
@Data
public class CalculateFeeDTO {

    @NotBlank(message = "Vui lòng nhập địa chỉ lấy hàng")
    private String pickupAddress;

    @NotBlank(message = "Vui lòng nhập địa chỉ giao hàng")
    private String deliveryAddress;

    private String serviceType = "DELIVERY"; // DELIVERY, BUY_FOR, BUS_PICKUP, DRIVER_HELP

    private Integer surcharge = 0; // Phụ phí (ví dụ: phí mưa 5000đ)
}
