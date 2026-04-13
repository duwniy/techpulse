package com.techpulse.onboarding;

import com.techpulse.user.User;
import com.techpulse.user.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OnboardingController {

    private final OnboardingService onboardingService;
    private final UserRepository userRepository;

    @GetMapping("/onboarding/my")
    public ResponseEntity<Map<String, Object>> getMyPlan(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            OnboardingPlan plan = onboardingService.getPlanByEmployee(user.getId());
            return ResponseEntity.ok(Map.of("success", true, "data", plan));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @GetMapping("/materials")
    public ResponseEntity<Map<String, Object>> getMaterials(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<Material> materials = onboardingService.getAllMaterials();
            List<MaterialProgress> progress = List.of();

            if (userDetails != null) {
                User user = userRepository.findByEmail(userDetails.getUsername())
                        .orElseThrow(() -> new RuntimeException("User not found"));
                progress = onboardingService.getProgressByUser(user.getId());
            }
            
            return ResponseEntity.ok(Map.of(
                "success", true, 
                "data", Map.of(
                    "materials", materials,
                    "progress", progress
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @PatchMapping("/materials/{id}/status")
    public ResponseEntity<Map<String, Object>> updateMaterialStatus(
            @PathVariable UUID id,
            @RequestBody StatusUpdateResult request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            MaterialProgress progress = onboardingService.updateMaterialStatus(user.getId(), id, request.getStatus());
            return ResponseEntity.ok(Map.of("success", true, "data", progress));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @Data
    public static class StatusUpdateResult {
        private MaterialProgress.Status status;
    }
}
