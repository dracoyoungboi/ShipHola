package com.shiphola.dto.common;

import java.time.LocalDateTime;

/**
 * PackageFilterDTO - Filter cho danh sách Package
 * Dùng cho search, filter danh sách đơn hàng
 */
public class PackageFilterDTO {

    private String keyword;
    private String status;
    private String deliveryType;
    private String deliveryArea;
    private Long dispatcherId;
    private Long shipperId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDeliveryType() { return deliveryType; }
    public void setDeliveryType(String deliveryType) { this.deliveryType = deliveryType; }

    public String getDeliveryArea() { return deliveryArea; }
    public void setDeliveryArea(String deliveryArea) { this.deliveryArea = deliveryArea; }

    public Long getDispatcherId() { return dispatcherId; }
    public void setDispatcherId(Long dispatcherId) { this.dispatcherId = dispatcherId; }

    public Long getShipperId() { return shipperId; }
    public void setShipperId(Long shipperId) { this.shipperId = shipperId; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
}
