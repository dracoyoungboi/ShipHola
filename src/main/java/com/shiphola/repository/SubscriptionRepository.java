package com.shiphola.repository;

import com.shiphola.constant.SubscriptionPlan;
import com.shiphola.constant.SubscriptionStatus;
import com.shiphola.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByCompanyName(String companyName);

    @Query("SELECT s FROM Subscription s WHERE s.deleted = false AND s.status = 'ACTIVE' AND s.endDate > :now")
    List<Subscription> findActiveSubscriptions(@Param("now") LocalDateTime now);

    @Query("SELECT s FROM Subscription s WHERE s.deleted = false AND s.status = 'ACTIVE' AND s.endDate < :now")
    List<Subscription> findExpiredSubscriptions(@Param("now") LocalDateTime now);

    @Query("SELECT s FROM Subscription s WHERE s.deleted = false ORDER BY s.createdAt DESC")
    List<Subscription> findAllActive();

    @Query("SELECT s FROM Subscription s WHERE s.plan = :plan AND s.deleted = false")
    List<Subscription> findByPlan(@Param("plan") SubscriptionPlan plan);

    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.status = :status AND s.deleted = false")
    long countByStatus(@Param("status") SubscriptionStatus status);

    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.status = 'ACTIVE' AND s.deleted = false")
    long countActive();

    @Query("SELECT s FROM Subscription s WHERE s.companyName LIKE %:keyword% AND s.deleted = false")
    List<Subscription> searchByCompanyName(@Param("keyword") String keyword);
}
