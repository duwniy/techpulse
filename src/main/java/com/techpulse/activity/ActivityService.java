package com.techpulse.activity;

import com.techpulse.user.User;
import com.techpulse.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    public void logEvent(UUID userId, String eventType, Map<String, Object> metadata) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ActivityEvent event = ActivityEvent.builder()
                .user(user)
                .eventType(eventType)
                .metadata(metadata)
                .build();

        activityRepository.save(event);
    }
}
