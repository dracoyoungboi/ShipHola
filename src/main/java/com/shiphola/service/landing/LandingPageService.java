package com.shiphola.service.landing;

import com.shiphola.dto.request.ConfirmOrderDTO;
import com.shiphola.dto.request.PublicOrderDTO;
import com.shiphola.entity.Package;

/**
 * LandingPageService - Service cho Landing Page (Guest/Customer)
 * Xử lý các nghiệp vụ liên quan đến đặt đơn từ công chúng
 */
public interface LandingPageService {

    /**
     * Tạo đơn hàng từ landing page
     * @param dto Thông tin đơn hàng từ public
     * @return Package đã được tạo
     */
    Package createPublicOrder(PublicOrderDTO dto);

    /**
     * Xác nhận và lưu đơn hàng (Step 2 của flow 2-step)
     * @param dto Thông tin xác nhận đơn hàng
     * @return Package đã được tạo với tracking number
     */
    Package confirmOrder(ConfirmOrderDTO dto);

    /**
     * Tìm đơn hàng theo mã tracking hoặc số điện thoại
     * @param keyword Mã tracking hoặc SĐT
     * @return Danh sách đơn hàng khớp
     */
    java.util.List<Package> searchOrder(String keyword);
}
