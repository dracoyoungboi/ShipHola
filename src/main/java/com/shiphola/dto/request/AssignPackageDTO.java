package com.shiphola.dto.request;

import jakarta.validation.constraints.NotNull;

public class AssignPackageDTO {

    @NotNull(message = "Shipper không được để trống")
    private Long shipperId;

    private String note;

    public Long getShipperId() { return shipperId; }
    public void setShipperId(Long shipperId) { this.shipperId = shipperId; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
