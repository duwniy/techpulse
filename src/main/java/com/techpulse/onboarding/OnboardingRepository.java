package com.techpulse.onboarding;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OnboardingRepository extends JpaRepository<OnboardingPlan, UUID> {
    Optional<OnboardingPlan> findByEmployeeId(UUID employeeId);
    List<OnboardingPlan> findByManagerId(UUID managerId);
}
