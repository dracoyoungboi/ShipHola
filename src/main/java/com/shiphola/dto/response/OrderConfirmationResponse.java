package com.shiphola.dto.response;

import lombok.Data;

/**
 * OrderConfirmationResponse - DTO cho response xác nhận đặt đơn
 * Trả về từ Step 2: Xác nhận đặt đơn
 */
@Data
public class OrderConfirmationResponse {

    private boolean success;

    private String message;

    private String trackingNumber;

    private String hotline = "0909 xxx xxx"; // Sẽ config trong application.properties

    /**
     * Tạo response thành công
     */
    public static OrderConfirmationResponse success(String trackingNumber, String hotline) {
        OrderConfirmationResponse response = new OrderConfirmationResponse();
        response.setSuccess(true);
        response.setTrackingNumber(trackingNumber);
        response.setHotline(hotline);
        response.setMessage("Đơn hàng đã được ghi nhận thành công!");
        return response;
    }

    /**
     * Tạo response thất bại
     */
    public static OrderConfirmationResponse error(String message) {
        OrderConfirmationResponse response = new OrderConfirmationResponse();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }

    /**
     * Tạo response bị rate limit
     */
    public static OrderConfirmationResponse rateLimitError(int remainingMinutes) {
        OrderConfirmationResponse response = new OrderConfirmationResponse();
        response.setSuccess(false);
        response.setMessage("Bạn đã đặt đơn quá gần đây. Vui lòng đợi " + remainingMinutes + " phút nữa.");
        return response;
    }
}
