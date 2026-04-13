package com.techpulse.user;

import com.techpulse.onboarding.OnboardingPlan;
import com.techpulse.onboarding.OnboardingRepository;
import com.techpulse.risk.RiskEngineService;
import com.techpulse.risk.RiskRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
public class ManagerController {

    private final OnboardingRepository onboardingRepository;
    private final RiskRepository riskRepository;
    private final RiskEngineService riskEngineService;
    private final UserRepository userRepository;

    @GetMapping("/team")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Map<String, Object>> getTeam(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User manager = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            List<Map<String, Object>> team = onboardingRepository.findByManagerId(manager.getId()).stream()
                    .map(plan -> {
                        User employee = plan.getEmployee();
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", employee.getId());
                        map.put("fullName", employee.getFullName());
                        map.put("email", employee.getEmail());
                        map.put("risk", riskRepository.findTopByUserIdOrderByCalculatedAtDesc(employee.getId()).orElse(null));
                        map.put("dayCurrent", plan.getDayCurrent());
                        map.put("totalDays", plan.getTotalDays());
                        return map;
                    })
                    .toList();
            
            return ResponseEntity.ok(Map.of("success", true, "data", team));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @GetMapping("/employee/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Map<String, Object>> getEmployeeProfile(@PathVariable UUID id) {
        try {
            User employee = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
            OnboardingPlan plan = onboardingRepository.findByEmployeeId(id).orElse(null);
            
            Map<String, Object> profile = new HashMap<>();
            profile.put("user", employee);
            profile.put("plan", plan);
            profile.put("risk", riskRepository.findTopByUserIdOrderByCalculatedAtDesc(id).orElse(null));
            
            return ResponseEntity.ok(Map.of("success", true, "data", profile));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @PostMapping("/assign-mentor")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Map<String, Object>> assignMentor(@RequestBody MentorAssignmentRequest request) {
        try {
            OnboardingPlan plan = onboardingRepository.findByEmployeeId(request.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Plan not found"));
            
            User mentor = userRepository.findById(request.getMentorId())
                    .orElseThrow(() -> new RuntimeException("Mentor not found"));
            
            plan.setManager(mentor); // For simplicity, prompt says manager_id can be used as mentor
            onboardingRepository.save(plan);
            
            return ResponseEntity.ok(Map.of("success", true, "message", "Mentor assigned"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @Data
    public static class MentorAssignmentRequest {
        private UUID employeeId;
        private UUID mentorId;
    }
}
