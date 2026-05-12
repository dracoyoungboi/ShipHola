package com.shiphola.dto.request;

import com.shiphola.constant.SubscriptionPlan;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public class SubscriptionDTO {

    @NotBlank(message = "Tên công ty không được để trống")
    @Size(min = 2, max = 100, message = "Tên công ty từ 2-100 ký tự")
    private String companyName;

    @Size(max = 50, message = "Mã số thuế tối đa 50 ký tự")
    private String taxCode;

    @NotNull(message = "Gói đăng ký không được để trống")
    private SubscriptionPlan plan;

    @NotNull(message = "Số lượng gói hàng không được để trống")
    @Min(value = 1, message = "Tối thiểu 1 gói hàng")
    private Integer maxPackages;

    @NotNull(message = "Số lượng người dùng không được để trống")
    @Min(value = 1, message = "Tối thiểu 1 người dùng")
    private Integer maxUsers;

    @NotNull(message = "Phí hàng tháng không được để trống")
    @DecimalMin(value = "0", message = "Phí phải >= 0")
    private Double monthlyFee;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getTaxCode() { return taxCode; }
    public void setTaxCode(String taxCode) { this.taxCode = taxCode; }

    public SubscriptionPlan getPlan() { return plan; }
    public void setPlan(SubscriptionPlan plan) { this.plan = plan; }

    public Integer getMaxPackages() { return maxPackages; }
    public void setMaxPackages(Integer maxPackages) { this.maxPackages = maxPackages; }

    public Integer getMaxUsers() { return maxUsers; }
    public void setMaxUsers(Integer maxUsers) { this.maxUsers = maxUsers; }

    public Double getMonthlyFee() { return monthlyFee; }
    public void setMonthlyFee(Double monthlyFee) { this.monthlyFee = monthlyFee; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
}
