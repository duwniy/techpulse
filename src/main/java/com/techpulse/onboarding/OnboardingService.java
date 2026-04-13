package com.techpulse.onboarding;

import com.techpulse.activity.ActivityService;
import com.techpulse.user.User;
import com.techpulse.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OnboardingService {

    private final OnboardingRepository onboardingRepository;
    private final MaterialRepository materialRepository;
    private final MaterialProgressRepository progressRepository;
    private final UserRepository userRepository;
    private final ActivityService activityService;

    public OnboardingPlan getPlanByEmployee(UUID employeeId) {
        return onboardingRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Onboarding plan not found for employee: " + employeeId));
    }

    public List<Material> getAllMaterials() {
        return materialRepository.findAll();
    }

    public List<MaterialProgress> getProgressByUser(UUID userId) {
        return progressRepository.findByUserId(userId);
    }

    @Transactional
    public MaterialProgress updateMaterialStatus(UUID userId, UUID materialId, MaterialProgress.Status status) {
        MaterialProgress progress = progressRepository.findByUserIdAndMaterialId(userId, materialId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found: " + userId));
                    Material material = materialRepository.findById(materialId)
                            .orElseThrow(() -> new RuntimeException("Material not found: " + materialId));
                    return MaterialProgress.builder()
                            .user(user)
                            .material(material)
                            .status(MaterialProgress.Status.PENDING)
                            .build();
                });

        progress.setStatus(status);
        progress.setUpdatedAt(LocalDateTime.now());
        MaterialProgress saved = progressRepository.save(progress);

        activityService.logEvent(userId, "MATERIAL_STATUS_UPDATE", Map.of("materialId", materialId, "status", status));
        
        return saved;
    }
}
