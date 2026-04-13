package com.techpulse.auth;

import com.techpulse.user.User;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        try {
            Map<String, Object> response = authService.login(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(Map.of("success", true, "data", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        try {
            User user = authService.register(request.getEmail(), request.getPassword(), request.getFullName(), request.getRole());
            return ResponseEntity.ok(Map.of("success", true, "data", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @GetMapping("/dev-token/employee")
    public ResponseEntity<Map<String, Object>> getEmployeeDevToken() {
        return ResponseEntity.ok(Map.of("success", true, "data", buildDevToken(User.Role.EMPLOYEE)));
    }

    @GetMapping("/demo-profiles")
    public ResponseEntity<Map<String, Object>> getDemoProfiles() {
        try {
            return ResponseEntity.ok(Map.of("success", true, "data", authService.getDemoProfiles()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @GetMapping("/dev-token/hr")
    public ResponseEntity<Map<String, Object>> getHrDevToken() {
        return ResponseEntity.ok(Map.of("success", true, "data", buildDevToken(User.Role.HR)));
    }

    @GetMapping("/dev-token/manager")
    public ResponseEntity<Map<String, Object>> getManagerDevToken() {
        return ResponseEntity.ok(Map.of("success", true, "data", buildDevToken(User.Role.MANAGER)));
    }

    private Map<String, Object> buildDevToken(User.Role role) {
        String email = switch (role) {
            case HR -> "anna.smirnova@company.dev";
            case MANAGER -> "oleg.ivanov@company.dev";
            case EMPLOYEE -> "ivan.petrov@company.dev";
        };
        String token = authService.generateDevToken(email, role);
        return Map.of(
                "role", role,
                "email", email,
                "token", token,
                "authorizationHeader", "Bearer " + token
        );
    }

    @Data
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Data
    public static class RegisterRequest {
        private String email;
        private String password;
        private String fullName;
        private User.Role role;
    }
}
