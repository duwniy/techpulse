package com.techpulse.task;

import com.techpulse.task.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByPlanId(UUID planId);
    List<Task> findByPlanIdAndCompletedFalseAndDueDateBefore(UUID planId, LocalDate dueDate);
}
