package com.shiphola.dto.response;

public class DashboardStats {
    private long totalPackages;
    private long pendingPackages;
    private long assignedPackages;
    private long inTransitPackages;
    private long deliveredTodayPackages;
    private long totalUsers;
    private long totalSubscriptions;
    private long activeSubscriptions;

    public DashboardStats() {}

    public DashboardStats(long totalPackages, long pendingPackages, long assignedPackages,
                         long inTransitPackages, long deliveredTodayPackages,
                         long totalUsers, long totalSubscriptions, long activeSubscriptions) {
        this.totalPackages = totalPackages;
        this.pendingPackages = pendingPackages;
        this.assignedPackages = assignedPackages;
        this.inTransitPackages = inTransitPackages;
        this.deliveredTodayPackages = deliveredTodayPackages;
        this.totalUsers = totalUsers;
        this.totalSubscriptions = totalSubscriptions;
        this.activeSubscriptions = activeSubscriptions;
    }

    public long getTotalPackages() { return totalPackages; }
    public void setTotalPackages(long totalPackages) { this.totalPackages = totalPackages; }

    public long getPendingPackages() { return pendingPackages; }
    public void setPendingPackages(long pendingPackages) { this.pendingPackages = pendingPackages; }

    public long getAssignedPackages() { return assignedPackages; }
    public void setAssignedPackages(long assignedPackages) { this.assignedPackages = assignedPackages; }

    public long getInTransitPackages() { return inTransitPackages; }
    public void setInTransitPackages(long inTransitPackages) { this.inTransitPackages = inTransitPackages; }

    public long getDeliveredTodayPackages() { return deliveredTodayPackages; }
    public void setDeliveredTodayPackages(long deliveredTodayPackages) { this.deliveredTodayPackages = deliveredTodayPackages; }

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

    public long getTotalSubscriptions() { return totalSubscriptions; }
    public void setTotalSubscriptions(long totalSubscriptions) { this.totalSubscriptions = totalSubscriptions; }

    public long getActiveSubscriptions() { return activeSubscriptions; }
    public void setActiveSubscriptions(long activeSubscriptions) { this.activeSubscriptions = activeSubscriptions; }
}
