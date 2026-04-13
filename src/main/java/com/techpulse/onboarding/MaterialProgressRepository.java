package com.techpulse.onboarding;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MaterialProgressRepository extends JpaRepository<MaterialProgress, UUID> {
    List<MaterialProgress> findByUserId(UUID userId);
    Optional<MaterialProgress> findByUserIdAndMaterialId(UUID userId, UUID materialId);
}
