package com.techpulse.task;

import com.techpulse.onboarding.OnboardingPlan;
import com.techpulse.onboarding.OnboardingService;
import com.techpulse.user.User;
import com.techpulse.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final OnboardingService onboardingService;
    private final UserRepository userRepository;

    @GetMapping("/my")
    public ResponseEntity<Map<String, Object>> getMyTasks(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            OnboardingPlan plan = onboardingService.getPlanByEmployee(user.getId());
            List<Task> tasks = taskService.getTasksByPlan(plan.getId());
            return ResponseEntity.ok(Map.of("success", true, "data", tasks));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<Map<String, Object>> completeTask(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Task task = taskService.completeTask(id, user.getId());
            return ResponseEntity.ok(Map.of("success", true, "data", task));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}
