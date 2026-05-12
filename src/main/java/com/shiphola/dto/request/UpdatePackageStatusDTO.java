package com.shiphola.dto.request;

import jakarta.validation.constraints.NotBlank;

public class UpdatePackageStatusDTO {

    @NotBlank(message = "Trạng thái không được để trống")
    private String status;

    private String note;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
