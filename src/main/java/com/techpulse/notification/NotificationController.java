package com.techpulse.notification;

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
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getMyNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            List<Notification> notifications = notificationService.getNotificationsForManager(user.getId());
            return ResponseEntity.ok(Map.of("success", true, "data", notifications));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/dismiss")
    public ResponseEntity<Map<String, Object>> dismissNotification(
            @PathVariable UUID id) {
        try {
            notificationService.dismissNotification(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Notification dismissed"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}
