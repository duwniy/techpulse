package com.techpulse.activity;

import com.techpulse.activity.ActivityEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ActivityRepository extends JpaRepository<ActivityEvent, UUID> {
    Optional<ActivityEvent> findTopByUserIdOrderByCreatedAtDesc(UUID userId);
    List<ActivityEvent> findByUserIdAndCreatedAtAfter(UUID userId, LocalDateTime since);
}
