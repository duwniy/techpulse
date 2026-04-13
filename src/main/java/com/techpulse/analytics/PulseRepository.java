package com.techpulse.analytics;

import com.techpulse.analytics.PulseResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PulseRepository extends JpaRepository<PulseResponse, UUID> {
    List<PulseResponse> findByUserIdAndCreatedAtAfter(UUID userId, LocalDateTime since);
}
