package com.shiphola.dto.response;

import lombok.Data;

/**
 * FeeCalculationResponse - DTO cho response tính phí ship
 * Trả về từ Step 1: Tính giá cước
 */
@Data
public class FeeCalculationResponse {

    /**
     * Khoảng cách tính bằng km (đã làm tròn 0.5km)
     */
    private Double distance;

    /**
     * Thời gian ước tính (phút)
     */
    private Integer duration;

    /**
     * Phí ship (VNĐ)
     */
    private Double fee;

    /**
     * Phụ phí (VNĐ)
     */
    private Integer surcharge = 0;

    /**
     * Tổng phí = fee + surcharge
     */
    public Integer getTotalFee() {
        if (fee == null) {
            return surcharge;
        }
        return (int) Math.round(fee + surcharge);
    }

    /**
     * Message hiển thị cho user
     */
    public String getDisplayMessage() {
        if (fee == null) {
            return "Khoảng cách quá xa (>7km). Vui lòng liên hệ hotline để thỏa thuận giá.";
        }
        return String.format("Khoảng cách: %.1f km | Thời gian: %d phút | Phí ship: %s đ",
                distance, duration, getTotalFee());
    }
}
