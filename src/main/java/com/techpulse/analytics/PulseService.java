package com.techpulse.analytics;

import com.techpulse.activity.ActivityService;
import com.techpulse.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PulseService {

    private final PulseRepository pulseRepository;
    private final ActivityService activityService;

    public void submitResponse(User user, Integer workloadRating, String openText) {
        PulseResponse response = PulseResponse.builder()
                .user(user)
                .workloadRating(workloadRating)
                .openText(openText)
                .build();
        
        pulseRepository.save(response);
        activityService.logEvent(user.getId(), "PULSE_SUBMIT", Map.of("workloadRating", workloadRating));
    }
}
