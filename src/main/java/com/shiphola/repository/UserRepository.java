package com.shiphola.repository;

import com.shiphola.constant.Role;
import com.shiphola.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);

    @Query("SELECT u FROM User u WHERE u.provider = :provider AND u.providerId = :providerId")
    Optional<User> findByProviderAndProviderId(@Param("provider") String provider, @Param("providerId") String providerId);

    List<User> findByRole(Role role);

    @Query("SELECT u FROM User u WHERE u.deleted = false AND u.role = :role ORDER BY u.createdAt DESC")
    List<User> findByRoleAndNotDeleted(@Param("role") Role role);

    @Query("SELECT u FROM User u WHERE u.deleted = false ORDER BY u.createdAt DESC")
    List<User> findAllActive();

    @Query("SELECT u FROM User u WHERE u.subscriptionId = :subscriptionId AND u.deleted = false")
    List<User> findBySubscriptionId(@Param("subscriptionId") Long subscriptionId);

    @Query("SELECT COUNT(u) FROM User u WHERE u.deleted = false")
    long countActive();

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.deleted = false")
    long countByRole(@Param("role") Role role);

    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :since AND u.deleted = false")
    long countNewUsersSince(@Param("since") LocalDateTime since);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
