package com.shiphola.dto.request;

import jakarta.validation.constraints.*;
import java.util.List;

/**
 * CreatePackageDTO - DTO cho tạo gói hàng mới
 * Dùng để hứng dữ liệu từ form web submit
 */
public class CreatePackageDTO {

    @NotBlank(message = "Tên gói hàng không được để trống")
    @Size(min = 5, max = 100, message = "Tên gói hàng phải từ 5-100 ký tự")
    private String packageName;

    @NotBlank(message = "Người gửi không được để trống")
    @Size(min = 2, max = 50, message = "Tên người gửi từ 2-50 ký tự")
    private String senderName;

    @NotBlank(message = "SĐT người gửi không được để trống")
    @Pattern(regexp = "^[0-9]{10}$", message = "SĐT phải là 10 số")
    private String senderPhone;

    @NotBlank(message = "Địa chỉ người gửi không được để trống")
    @Size(min = 10, max = 200, message = "Địa chỉ từ 10-200 ký tự")
    private String senderAddress;

    @NotBlank(message = "Người nhận không được để trống")
    @Size(min = 2, max = 50, message = "Tên người nhận từ 2-50 ký tự")
    private String receiverName;

    @NotBlank(message = "SĐT người nhận không được để trống")
    @Pattern(regexp = "^[0-9]{10}$", message = "SĐT phải là 10 số")
    private String receiverPhone;

    @NotBlank(message = "Địa chỉ người nhận không được để trống")
    @Size(min = 10, max = 200, message = "Địa chỉ từ 10-200 ký tự")
    private String receiverAddress;

    @NotNull(message = "Trọng lượng không được để trống")
    @DecimalMin(value = "0.1", message = "Trọng lượng phải >= 0.1kg")
    @DecimalMax(value = "1000", message = "Trọng lượng phải <= 1000kg")
    private Double weight;

    @Size(max = 500, message = "Nội dung gói hàng tối đa 500 ký tự")
    private String packageContent;

    @Size(max = 500, message = "Hướng dẫn tối đa 500 ký tự")
    private String specialInstructions;

    @NotBlank(message = "Loại giao hàng không được để trống")
    private String deliveryType;

    @NotNull(message = "Khu vực giao hàng không được để trống")
    private String deliveryArea;

    private Double shippingFee;

    // ==========================================
    // GETTERS & SETTERS
    // ==========================================

    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getSenderPhone() { return senderPhone; }
    public void setSenderPhone(String senderPhone) { this.senderPhone = senderPhone; }

    public String getSenderAddress() { return senderAddress; }
    public void setSenderAddress(String senderAddress) { this.senderAddress = senderAddress; }

    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public String getReceiverPhone() { return receiverPhone; }
    public void setReceiverPhone(String receiverPhone) { this.receiverPhone = receiverPhone; }

    public String getReceiverAddress() { return receiverAddress; }
    public void setReceiverAddress(String receiverAddress) { this.receiverAddress = receiverAddress; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public String getPackageContent() { return packageContent; }
    public void setPackageContent(String packageContent) { this.packageContent = packageContent; }

    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }

    public String getDeliveryType() { return deliveryType; }
    public void setDeliveryType(String deliveryType) { this.deliveryType = deliveryType; }

    public String getDeliveryArea() { return deliveryArea; }
    public void setDeliveryArea(String deliveryArea) { this.deliveryArea = deliveryArea; }

    public Double getShippingFee() { return shippingFee; }
    public void setShippingFee(Double shippingFee) { this.shippingFee = shippingFee; }
}
