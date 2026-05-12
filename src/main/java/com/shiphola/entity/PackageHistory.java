package com.shiphola.entity;

import com.shiphola.constant.OrderStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "package_history")
public class PackageHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id")
    private Package packageEntity;

    @Enumerated(EnumType.STRING)
    private OrderStatus fromStatus;

    @Enumerated(EnumType.STRING)
    private OrderStatus toStatus;

    private String note;
    private String updatedBy;

    private LocalDateTime createdAt;

    public PackageHistory() {
        this.createdAt = LocalDateTime.now();
    }

    public PackageHistory(Package packageEntity, OrderStatus fromStatus, OrderStatus toStatus, String note, String updatedBy) {
        this.packageEntity = packageEntity;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.note = note;
        this.updatedBy = updatedBy;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getHistoryId() { return historyId; }
    public void setHistoryId(Long historyId) { this.historyId = historyId; }

    public Package getPackageEntity() { return packageEntity; }
    public void setPackageEntity(Package packageEntity) { this.packageEntity = packageEntity; }

    public OrderStatus getFromStatus() { return fromStatus; }
    public void setFromStatus(OrderStatus fromStatus) { this.fromStatus = fromStatus; }

    public OrderStatus getToStatus() { return toStatus; }
    public void setToStatus(OrderStatus toStatus) { this.toStatus = toStatus; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
