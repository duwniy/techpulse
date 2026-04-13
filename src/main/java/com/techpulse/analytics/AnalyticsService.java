package com.techpulse.analytics;

import com.techpulse.risk.RiskRepository;
import com.techpulse.risk.RiskScore;
import com.techpulse.user.User;
import com.techpulse.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final UserRepository userRepository;
    private final RiskRepository riskRepository;

    public Map<String, Object> getHrDashboardStats() {
        List<User> employees = userRepository.findAll().stream()
                .filter(u -> u.getRole() == User.Role.EMPLOYEE)
                .toList();
        
        long totalEmployees = employees.size();
        
        Map<RiskScore.RiskLevel, Long> riskDistribution = employees.stream()
                .map(e -> riskRepository.findTopByUserIdOrderByCalculatedAtDesc(e.getId()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(RiskScore::getLevel, Collectors.counting()));

        return Map.of(
                "totalEmployees", totalEmployees,
                "riskDistribution", riskDistribution,
                "activeRisks", riskDistribution.getOrDefault(RiskScore.RiskLevel.RED, 0L) + riskDistribution.getOrDefault(RiskScore.RiskLevel.AMBER, 0L)
        );
    }

    public List<Map<String, Object>> getAllEmployeesWithRisk() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == User.Role.EMPLOYEE)
                .map(u -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", u.getId());
                    map.put("fullName", u.getFullName());
                    map.put("email", u.getEmail());
                    map.put("avatarInitials", u.getAvatarInitials());
                    map.put("risk", riskRepository.findTopByUserIdOrderByCalculatedAtDesc(u.getId()).orElse(null));
                    return map;
                })
                .toList();
    }

    public Map<RiskScore.RiskLevel, Long> getRiskSummary() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == User.Role.EMPLOYEE)
                .map(e -> riskRepository.findTopByUserIdOrderByCalculatedAtDesc(e.getId()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(RiskScore::getLevel, Collectors.counting()));
    }
}
