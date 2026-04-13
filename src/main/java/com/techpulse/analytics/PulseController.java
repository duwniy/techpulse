package com.techpulse.analytics;

import com.techpulse.user.User;
import com.techpulse.user.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/pulse")
@RequiredArgsConstructor
public class PulseController {

    private final PulseService pulseService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Map<String, Object>> submitPulse(
            @RequestBody PulseRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            pulseService.submitResponse(user, request.getWorkloadRating(), request.getOpenText());
            return ResponseEntity.ok(Map.of("success", true, "message", "Pulse response submitted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @Data
    public static class PulseRequest {
        private Integer workloadRating;
        private String openText;
    }
}
