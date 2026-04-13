package com.techpulse.risk;

import com.techpulse.user.User;
import com.techpulse.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/risk")
@RequiredArgsConstructor
public class RiskController {

    private final RiskRepository riskRepository;
    private final RiskEngineService riskEngineService;
    private final UserRepository userRepository;

    @GetMapping("/my")
    public ResponseEntity<Map<String, Object>> getMyRisk(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            return riskRepository.findTopByUserIdOrderByCalculatedAtDesc(user.getId())
                    .map(score -> ResponseEntity.ok(Map.of("success", true, "data", score)))
                    .orElseGet(() -> {
                        // If no score yet, calculate on the fly
                        RiskScore newScore = riskEngineService.calculateRiskForUser(user.getId());
                        return ResponseEntity.ok(Map.of("success", true, "data", newScore));
                    });
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUserRisk(@PathVariable UUID userId) {
        try {
            return riskRepository.findTopByUserIdOrderByCalculatedAtDesc(userId)
                    .map(score -> ResponseEntity.ok(Map.of("success", true, "data", score)))
                    .orElseGet(() -> {
                        RiskScore newScore = riskEngineService.calculateRiskForUser(userId);
                        return ResponseEntity.ok(Map.of("success", true, "data", newScore));
                    });
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @org.springframework.web.bind.annotation.PostMapping("/recalculate")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('HR', 'MANAGER')")
    public ResponseEntity<Map<String, Object>> recalculateAll() {
        try {
            riskEngineService.recalculateAllRisks();
            return ResponseEntity.ok(Map.of("success", true, "message", "Risk recalculation started"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}

