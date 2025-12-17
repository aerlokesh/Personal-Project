package com.aerloki.personal.project.Personal.Project.repository.view;

import com.aerloki.personal.project.Personal.Project.model.view.UserOrderSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for UserOrderSummary Materialized View
 * Provides fast read access to aggregated user order data
 */
@Repository
public interface UserOrderSummaryRepository extends JpaRepository<UserOrderSummary, Long> {
    
    /**
     * Find user order summary by user ID
     */
    @Query("SELECT u FROM UserOrderSummary u WHERE u.userId = :userId")
    Optional<UserOrderSummary> findByUserId(@Param("userId") Long userId);
    
    /**
     * Find user order summary by email
     */
    @Query("SELECT u FROM UserOrderSummary u WHERE u.email = :email")
    Optional<UserOrderSummary> findByEmail(@Param("email") String email);
}
