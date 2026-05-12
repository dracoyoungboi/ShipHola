package com.shiphola.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Package Entity - Gói hàng Logistics
 *
 * Workflow:
 * 1. Admin tạo Package (gói hàng)
 * 2. Dispatcher lên đơn (assign Package cho Shipper)
 * 3. Shipper nhận đơn (nhận Package để giao)
 * 4. Shipper cập nhật trạng thái giao hàng
 */
@Entity
@Table(name = "packages")
public class Package {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long packageId;

    // Thông tin cơ bản
    private String packageName;        // Tên gói hàng
    private String trackingNumber;      // Mã vận đơn

    // Thông tin người gửi
    private String senderName;
    private String senderPhone;
    private String senderAddress;
    private String senderWard;

    // Thông tin người nhận
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;

    // Thông tin gói hàng
    private Double weight;              // Trọng lượng (kg)
    private Double dimensions;          // Kích thước (DxRxC cm)
    private String packageContent;      // Nội dung gói hàng
    private String specialInstructions; // Hướng dẫn đặc biệt

    // Thông tin giao hàng
    private String deliveryType;       // STANDARD, EXPRESS, SAME_DAY
    private Double shippingFee;         // Phí ship
    private String deliveryArea;       // Khu vực giao (North, South, East, West)

    // Trạng thái đơn hàng
    // PENDING - Chờ xử lý
    // ASSIGNED - Đã giao cho Shipper
    // PICKED_UP - Shipper đã lấy hàng
    // IN_TRANSIT - Đang giao hàng
    // DELIVERED - Đã giao thành công
    // FAILED - Giao hàng thất bại
    // CANCELLED - Đã hủy
    private String status;

    // Thời gian
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime estimatedDeliveryTime;
    private LocalDateTime deliveredAt;

    // Subscription info
    private Long subscriptionId;       // FK đến subscription (nếu có)

    // Soft delete
    private Boolean deleted = false;

    // ==========================================
    // RELATIONSHIPS
    // ==========================================

    @ManyToOne
    @JoinColumn(name = "dispatcher_id")
    private User dispatcher;

    @ManyToOne
    @JoinColumn(name = "shipper_id")
    private User shipper;

    @OneToMany(mappedBy = "packageEntity", cascade = CascadeType.ALL)
    private List<PackageHistory> history;

    // ==========================================
    // CONSTRUCTORS
    // ==========================================

    public Package() {
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // ==========================================
    // GETTERS & SETTERS
    // ==========================================

    public Long getPackageId() { return packageId; }
    public void setPackageId(Long packageId) { this.packageId = packageId; }

    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getSenderPhone() { return senderPhone; }
    public void setSenderPhone(String senderPhone) { this.senderPhone = senderPhone; }

    public String getSenderAddress() { return senderAddress; }
    public void setSenderAddress(String senderAddress) { this.senderAddress = senderAddress; }

    public String getSenderWard() { return senderWard; }
    public void setSenderWard(String senderWard) { this.senderWard = senderWard; }

    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public String getReceiverPhone() { return receiverPhone; }
    public void setReceiverPhone(String receiverPhone) { this.receiverPhone = receiverPhone; }

    public String getReceiverAddress() { return receiverAddress; }
    public void setReceiverAddress(String receiverAddress) { this.receiverAddress = receiverAddress; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public Double getDimensions() { return dimensions; }
    public void setDimensions(Double dimensions) { this.dimensions = dimensions; }

    public String getPackageContent() { return packageContent; }
    public void setPackageContent(String packageContent) { this.packageContent = packageContent; }

    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }

    public String getDeliveryType() { return deliveryType; }
    public void setDeliveryType(String deliveryType) { this.deliveryType = deliveryType; }

    public Double getShippingFee() { return shippingFee; }
    public void setShippingFee(Double shippingFee) { this.shippingFee = shippingFee; }

    public String getDeliveryArea() { return deliveryArea; }
    public void setDeliveryArea(String deliveryArea) { this.deliveryArea = deliveryArea; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getEstimatedDeliveryTime() { return estimatedDeliveryTime; }
    public void setEstimatedDeliveryTime(LocalDateTime estimatedDeliveryTime) { this.estimatedDeliveryTime = estimatedDeliveryTime; }

    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }

    public Long getSubscriptionId() { return subscriptionId; }
    public void setSubscriptionId(Long subscriptionId) { this.subscriptionId = subscriptionId; }

    public Boolean getDeleted() { return deleted; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }

    public User getDispatcher() { return dispatcher; }
    public void setDispatcher(User dispatcher) { this.dispatcher = dispatcher; }

    public User getShipper() { return shipper; }
    public void setShipper(User shipper) { this.shipper = shipper; }

    public List<PackageHistory> getHistory() { return history; }
    public void setHistory(List<PackageHistory> history) { this.history = history; }

    // ==========================================
    // HELPER METHODS
    // // ==========================================

    /**
     * Kiểm tra package có thể được giao cho shipper không
     */
    public boolean canBeAssigned() {
        return "PENDING".equals(status) &&
               dispatcher == null &&
               !Boolean.TRUE.equals(deleted);
    }

    /**
     * Kiểm tra shipper có thể cập nhật trạng thái không
     */
    public boolean canShipperUpdate() {
        return !"PENDING".equals(status) &&
               shipper != null &&
               !Boolean.TRUE.equals(deleted);
    }

    /**
     * Tạo tracking number tự động
     */
    public String generateTrackingNumber() {
        return "SH" + System.currentTimeMillis() + randomDigits(6);
    }

    private String randomDigits(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((int)(Math.random() * 10));
        }
        return sb.toString();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (status == null) {
            status = "PENDING";
        }

        if (trackingNumber == null || trackingNumber.isEmpty()) {
            trackingNumber = generateTrackingNumber();
        }

        if (deleted == null) {
            deleted = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
