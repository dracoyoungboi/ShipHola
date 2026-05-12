package com.shiphola.util;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RateLimitUtil - Utility cho Rate Limiting để bảo vệ khỏi DDOS attack
 * Sử dụng in-memory ConcurrentHashMap để lưu timestamps của requests
 * Rate limit: 1 request / 5 phút / IP
 */
@Component
public class RateLimitUtil {

    private final ConcurrentHashMap<String, LocalDateTime> requestTimestamps = new ConcurrentHashMap<>();

    private static final int RATE_LIMIT_MINUTES = 5;

    /**
     * Kiểm tra xem IP có được phép đặt đơn hay không
     * @param ip Địa chỉ IP của client
     * @return true nếu được phép, false nếu bị rate limit
     */
    public boolean isAllowed(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastRequest = requestTimestamps.get(ip);

        if (lastRequest == null) {
            // First request from this IP
            requestTimestamps.put(ip, now);
            return true;
        }

        long minutesSinceLastRequest = Duration.between(lastRequest, now).toMinutes();

        if (minutesSinceLastRequest >= RATE_LIMIT_MINUTES) {
            // Update timestamp and allow
            requestTimestamps.put(ip, now);
            return true;
        }

        // Still within rate limit window
        return false;
    }

    /**
     * Lấy số phút còn lại trước khi được phép đặt đơn tiếp theo
     * @param ip Địa chỉ IP của client
     * @return Số phút còn lại
     */
    public int getRemainingMinutes(String ip) {
        if (ip == null || ip.isEmpty()) {
            return RATE_LIMIT_MINUTES;
        }

        LocalDateTime lastRequest = requestTimestamps.get(ip);
        if (lastRequest == null) {
            return 0;
        }

        long minutesSinceLastRequest = Duration.between(lastRequest, LocalDateTime.now()).toMinutes();
        int remaining = RATE_LIMIT_MINUTES - (int) minutesSinceLastRequest;

        return Math.max(0, remaining);
    }

    /**
     * Xóa IP khỏi danh sách rate limit (để test hoặc admin reset)
     * @param ip Địa chỉ IP cần xóa
     */
    public void reset(String ip) {
        if (ip != null) {
            requestTimestamps.remove(ip);
        }
    }

    /**
     * Xóa tất cả (để test)
     */
    public void resetAll() {
        requestTimestamps.clear();
    }
}
