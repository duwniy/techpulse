package com.techpulse.risk;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface RiskRepository extends JpaRepository<RiskScore, UUID> {
    Optional<RiskScore> findTopByUserIdOrderByCalculatedAtDesc(UUID userId);
}
