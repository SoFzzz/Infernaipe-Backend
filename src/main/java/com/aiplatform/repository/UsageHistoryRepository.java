package com.aiplatform.repository;

import com.aiplatform.model.entity.UsageHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UsageHistoryRepository extends JpaRepository<UsageHistory, Long> {
    List<UsageHistory> findByUserIdOrderByDateDesc(String userId, Pageable pageable);

    Optional<UsageHistory> findByUserIdAndDate(String userId, LocalDate date);
}
