package com.shiphola.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * PublicOrderDTO - DTO cho đặt đơn từ Landing Page
 * Đơn giản hơn CreatePackageDTO, dành cho khách hàng public
 */
public class PublicOrderDTO {

    @NotBlank(message = "Vui lòng chọn loại dịch vụ")
    private String serviceType;       // DELIVERY, BUY_FOR, BUS_PICKUP, DRIVER_HELP

    @NotBlank(message = "Địa chỉ lấy hàng không được để trống")
    @Size(min = 10, max = 200, message = "Địa chỉ từ 10-200 ký tự")
    private String pickupAddress;

    @NotBlank(message = "Địa chỉ giao hàng không được để trống")
    @Size(min = 10, max = 200, message = "Địa chỉ từ 10-200 ký tự")
    private String deliveryAddress;

    private Double distance;          // Khoảng cách ước tính (km)

    @NotBlank(message = "SĐT người gửi không được để trống")
    @Pattern(regexp = "^[0-9]{10}$", message = "SĐT phải là 10 số")
    private String senderPhone;

    @NotBlank(message = "SĐT người nhận không được để trống")
    @Pattern(regexp = "^[0-9]{10}$", message = "SĐT phải là 10 số")
    private String receiverPhone;

    private Integer surcharge;        // Phụ phí mưa gió (0 hoặc 5000)

    @Size(max = 500, message = "Ghi chú tối đa 500 ký tự")
    private String note;

    // ==========================================
    // GETTERS & SETTERS
    // ==========================================

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }

    public String getSenderPhone() { return senderPhone; }
    public void setSenderPhone(String senderPhone) { this.senderPhone = senderPhone; }

    public String getReceiverPhone() { return receiverPhone; }
    public void setReceiverPhone(String receiverPhone) { this.receiverPhone = receiverPhone; }

    public Integer getSurcharge() { return surcharge; }
    public void setSurcharge(Integer surcharge) { this.surcharge = surcharge; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
