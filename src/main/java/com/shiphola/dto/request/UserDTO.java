package com.shiphola.dto.request;

import com.shiphola.constant.Role;
import jakarta.validation.constraints.*;
import java.util.List;

public class UserDTO {

    private Long userId;

    @NotBlank(groups = {Create.class}, message = "Username không được để trống")
    @Size(min = 3, max = 50, message = "Username phải từ 3-50 ký tự")
    private String username;

    @NotBlank(groups = {Create.class}, message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @Size(min = 6, max = 100, message = "Password phải từ 6-100 ký tự")
    private String password;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(min = 2, max = 100, message = "Họ tên từ 2-100 ký tự")
    private String fullName;

    @Pattern(regexp = "^[0-9]{10}$", message = "SĐT phải là 10 số")
    private String phone;

    @Size(max = 200, message = "Địa chỉ tối đa 200 ký tự")
    private String address;

    @NotNull(message = "Vai trò không được để trống")
    private Role role;

    private Long subscriptionId;

    private List<Long> packageIds;

    public interface Create {}

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Long getSubscriptionId() { return subscriptionId; }
    public void setSubscriptionId(Long subscriptionId) { this.subscriptionId = subscriptionId; }

    public List<Long> getPackageIds() { return packageIds; }
    public void setPackageIds(List<Long> packageIds) { this.packageIds = packageIds; }
}
