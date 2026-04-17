package com.aiplatform.repository;

import com.aiplatform.model.entity.UserQuota;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserQuotaRepository extends JpaRepository<UserQuota, Long> {
    Optional<UserQuota> findByUserId(String userId);
}
