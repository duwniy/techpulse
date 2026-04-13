package com.techpulse.task;

import com.techpulse.activity.ActivityService;
import com.techpulse.activity.ActivityEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ActivityService activityService;

    public List<Task> getTasksByPlan(UUID planId) {
        return taskRepository.findByPlanId(planId);
    }

    @Transactional
    public Task completeTask(UUID taskId, UUID userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        task.setCompleted(true);
        task.setCompletedAt(LocalDateTime.now());
        Task saved = taskRepository.save(task);

        activityService.logEvent(userId, "TASK_COMPLETE", Map.of("taskId", taskId));
        
        return saved;
    }
}
