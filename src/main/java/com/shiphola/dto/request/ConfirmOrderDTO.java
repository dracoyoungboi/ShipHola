package com.shiphola.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * ConfirmOrderDTO - DTO cho request xác nhận đặt đơn
 * Sử dụng trong Step 2: Xác nhận đặt đơn (có lưu DB)
 * Chỉ có IP và feeId - dữ liệu thực tế đã được lưu trong cache
 */
@Data
public class ConfirmOrderDTO {

    @NotBlank(message = "Địa chỉ lấy hàng không được để trống")
    private String pickupAddress;

    @NotBlank(message = "Địa chỉ giao hàng không được để trống")
    private String deliveryAddress;

    @Pattern(regexp = "^[0-9]{10}$", message = "Số điện thoại người gửi phải có 10 chữ số")
    private String senderPhone;

    @Pattern(regexp = "^[0-9]{10}$", message = "Số điện thoại người nhận phải có 10 chữ số")
    private String receiverPhone;

    private String serviceType = "DELIVERY";

    private Integer surcharge = 0;

    private String note;

    // Distance đã được tính từ Step 1
    private Double distance;

    // Fee đã được tính từ Step 1
    private Integer calculatedFee;
}
