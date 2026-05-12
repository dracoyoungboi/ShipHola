package com.shiphola.service;

import com.shiphola.dto.response.FeeCalculationResponse;

/**
 * MapService - Service cho các thao tác liên quan đến bản đồ
 * Tích hợp OpenStreetMap APIs (Nominatim, OSRM)
 */
public interface MapService {

    /**
     * Reverse geocoding: Tìm địa chỉ từ tọa độ (lat, lon)
     * @param lat Vĩ độ
     * @param lon Kinh độ
     * @return Địa chỉ đầy đủ
     */
    String reverseGeocode(double lat, double lon);

    /**
     * Forward geocoding: Tìm tọa độ từ địa chỉ
     * @param address Địa chỉ cần tìm
     * @return Mảng double [lat, lon] hoặc null nếu không tìm thấy
     */
    double[] forwardGeocode(String address);

    /**
     * Tính khoảng cách và thời gian di chuyển giữa 2 điểm
     * Sử dụng OSRM API với alternatives để lấy cả đường ngắn nhất và dài nhất
     * @param lat1 Vĩ độ điểm đi
     * @param lon1 Kinh độ điểm đi
     * @param lat2 Vĩ độ điểm đến
     * @param lon2 Kinh độ điểm đến
     * @return FeeCalculationResponse chứa distance (km), duration (phút)
     */
    FeeCalculationResponse calculateDistanceAndDuration(double lat1, double lon1, double lat2, double lon2);

    /**
     * Tính phí ship dựa trên khoảng cách
     * @param distanceKm Khoảng cách tính bằng km
     * @return Phí ship (VNĐ)
     */
    Double calculateFee(double distanceKm);

    /**
     * Tính phí ship đầy đủ: geocode cả 2 địa chỉ + tính khoảng cách + tính phí
     * @param pickupAddress Địa chỉ lấy hàng
     * @param deliveryAddress Địa chỉ giao hàng
     * @return FeeCalculationResponse hoặc null nếu không thể tính
     */
    FeeCalculationResponse calculateFeeFromAddresses(String pickupAddress, String deliveryAddress);
}
