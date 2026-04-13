package com.techpulse.risk;

import com.techpulse.activity.ActivityRepository;
import com.techpulse.analytics.PulseRepository;
import com.techpulse.analytics.PulseResponse;
import com.techpulse.notification.NotificationService;
import com.techpulse.onboarding.OnboardingPlan;
import com.techpulse.onboarding.OnboardingRepository;
import com.techpulse.task.TaskRepository;
import com.techpulse.user.User;
import com.techpulse.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiskEngineService {

    private final RiskRepository riskRepository;
    private final ActivityRepository activityRepository;
    private final TaskRepository taskRepository;
    private final PulseRepository pulseRepository;
    private final OnboardingRepository onboardingRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Scheduled(fixedRate = 21600000) // 6 hours
    public void recalculateAllRisks() {
        log.info("Starting scheduled risk recalculation");
        List<User> employees = userRepository.findAll().stream()
                .filter(u -> u.getRole() == User.Role.EMPLOYEE)
                .toList();
        
        for (User employee : employees) {
            try {
                calculateRiskForUser(employee.getId());
            } catch (Exception e) {
                log.error("Failed to calculate risk for user {}: {}", employee.getId(), e.getMessage());
            }
        }
    }

    @Transactional
    public RiskScore calculateRiskForUser(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow();
        OnboardingPlan plan = onboardingRepository.findByEmployeeId(userId).orElse(null);
        
        int score = 0;
        Map<String, Object> signals = new HashMap<>();

        // 1. Last Login Signal (+30)
        activityRepository.findTopByUserIdOrderByCreatedAtDesc(userId).ifPresentOrElse(event -> {
            if (event.getCreatedAt().isBefore(LocalDateTime.now().minusDays(3))) {
                signals.put("NO_LOGIN_3D", 30);
            }
        }, () -> signals.put("NEVER_LOGGED_IN", 30));

        // 2. Overdue Tasks Signal (+25)
        if (plan != null) {
            long overdueCount = taskRepository.findByPlanIdAndCompletedFalseAndDueDateBefore(plan.getId(), LocalDate.now()).size();
            if (overdueCount >= 2) {
                signals.put("OVERDUE_TASKS_2P", 25);
            }
        }

        // 3. Low Pulse Rating Signal (+20)
        List<PulseResponse> recentPulses = pulseRepository.findByUserIdAndCreatedAtAfter(userId, LocalDateTime.now().minusDays(7));
        boolean lowPulse = recentPulses.stream().anyMatch(p -> p.getWorkloadRating() != null && p.getWorkloadRating() <= 2);
        if (lowPulse) {
            signals.put("LOW_PULSE_RATING", 20);
        }

        // 4. Activity Drop Signal (+15)
        long recentEvents = activityRepository.findByUserIdAndCreatedAtAfter(userId, LocalDateTime.now().minusDays(3)).size();
        if (recentEvents < 5) {
            signals.put("LOW_ACTIVITY_3D", 15);
        }

        // Sum up score
        for (Object s : signals.values()) {
            if (s instanceof Integer) {
                score += (Integer) s;
            }
        }

        // Contradiction detection
        boolean highPulse = recentPulses.stream().anyMatch(p -> p.getWorkloadRating() != null && p.getWorkloadRating() >= 4);
        if (highPulse && score > 50) {
            Integer pulseWeight = (Integer) signals.getOrDefault("LOW_PULSE_RATING", 0);
            int behavioralScore = score - pulseWeight;
            score = (int) (behavioralScore * 1.5);
            signals.put("CONTRADICTION_DETECTED", true);
        }

        RiskScore.RiskLevel level = RiskScore.RiskLevel.GREEN;
        if (score > 60) level = RiskScore.RiskLevel.RED;
        else if (score > 30) level = RiskScore.RiskLevel.AMBER;

        RiskScore riskScore = RiskScore.builder()
                .user(user)
                .score(score)
                .level(level)
                .signals(signals)
                .calculatedAt(LocalDateTime.now())
                .build();
        
        RiskScore saved = riskRepository.save(riskScore);

        if (plan != null && plan.getManager() != null && (level == RiskScore.RiskLevel.AMBER || level == RiskScore.RiskLevel.RED)) {
            String message = level == RiskScore.RiskLevel.RED 
                    ? "Critical risk level detected for " + user.getFullName() + ". Action recommended."
                    : "Risk level Amber for " + user.getFullName() + ". Please check in.";
            notificationService.createNotification(plan.getManager(), user, "RISK_" + level, message);
        }

        return saved;
    }
}
