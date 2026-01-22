package org.example.service.search;

import org.example.domain.task.Task;
import org.example.domain.task.TaskStatus;
import org.example.domain.task.Priority;
import org.example.repository.TaskRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class TaskSearchService {

    private final TaskRepository taskRepository = new TaskRepository();

    public List<Task> findByStatus(TaskStatus... statuses) {
        Set<TaskStatus> set = Set.of(statuses);

        return taskRepository.findAll().stream()
                .filter(t -> set.contains(t.getStatus()))
                .collect(Collectors.toList());
    }

    public List<Task> findByPriority(Priority... priorities) {
        Set<Priority> set = Set.of(priorities);

        return taskRepository.findAll().stream()
                .filter(t -> set.contains(t.getPriority()))
                .collect(Collectors.toList());
    }

    public List<Task> findByAssigneeEmail(String email) {
        return taskRepository.findAll().stream()
                .filter(t -> t.getAssignedTo().isPresent())
                .filter(t -> t.getAssignedTo().get().getEmail().equals(email))
                .collect(Collectors.toList());
    }

    public List<Task> findByCreatorEmail(String email) {
        return taskRepository.findAll().stream()
                .filter(t -> t.getCreatedBy().getEmail().equals(email))
                .collect(Collectors.toList());
    }

    public List<Task> findOverdue() {
        LocalDate today = LocalDate.now();

        return taskRepository.findAll().stream()
                .filter(t -> t.getDueDate()
                        .map(d->d.isBefore(today.atStartOfDay()))
                        .orElse(false))
                .filter(t -> t.getStatus() != TaskStatus.COMPLETED)
                .filter(t -> t.getStatus() != TaskStatus.CANCELLED)
                .collect(Collectors.toList());
    }

    public List<Task> findByTag(String tag) {
        return taskRepository.findAll().stream()
                .filter(t -> t.getTags().contains(tag))
                .collect(Collectors.toList());
    }


    private Comparator<Task> buildComparator(TaskSearchCriteria c) {
        if (c.sortField == null) return (a, b) -> 0;

        Comparator<Task> comp = switch (c.sortField) {
            case PRIORITY -> Comparator.comparing(Task::getPriority);
            case DUE_DATE -> Comparator.comparing(
                    (Task t) -> t.getDueDate().orElse(LocalDateTime.MAX)
            );
            case CREATED_AT -> Comparator.comparing(Task::getCreatedAt);
            case STATUS -> Comparator.comparing(Task::getStatus);
        };

        return c.ascending ? comp : comp.reversed();
    }

}
