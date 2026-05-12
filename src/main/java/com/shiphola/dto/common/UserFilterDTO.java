package com.shiphola.dto.common;

import com.shiphola.constant.Role;

/**
 * UserFilterDTO - Filter cho danh sách User
 * Dùng cho search, filter danh sách người dùng
 */
public class UserFilterDTO {

    private String keyword;
    private Role role;
    private Boolean enabled;
    private Long subscriptionId;

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public Long getSubscriptionId() { return subscriptionId; }
    public void setSubscriptionId(Long subscriptionId) { this.subscriptionId = subscriptionId; }
}
