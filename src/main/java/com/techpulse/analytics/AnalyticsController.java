package com.techpulse.analytics;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/hr/dashboard")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<Map<String, Object>> getHrDashboard() {
        try {
            return ResponseEntity.ok(Map.of("success", true, "data", analyticsService.getHrDashboardStats()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @GetMapping("/hr/employees")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<Map<String, Object>> getHrEmployees() {
        try {
            return ResponseEntity.ok(Map.of("success", true, "data", analyticsService.getAllEmployeesWithRisk()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @GetMapping("/hr/risk-summary")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<Map<String, Object>> getRiskSummary() {
        try {
            return ResponseEntity.ok(Map.of("success", true, "data", analyticsService.getRiskSummary()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}
