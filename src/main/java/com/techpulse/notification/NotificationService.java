package com.techpulse.notification;

import com.techpulse.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void createNotification(User manager, User employee, String type, String message) {
        Notification notification = Notification.builder()
                .manager(manager)
                .employee(employee)
                .type(type)
                .message(message)
                .dismissed(false)
                .build();
        notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsForManager(UUID managerId) {
        return notificationRepository.findByManagerIdAndDismissedFalse(managerId);
    }

    public void dismissNotification(UUID notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setDismissed(true);
            notificationRepository.save(n);
        });
    }
}
