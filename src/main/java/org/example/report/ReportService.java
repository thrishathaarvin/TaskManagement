package org.example.report;
import org.example.domain.task.Task;
import org.example.repository.TaskRepository;
import org.example.storage.InMemoryDatabase;
import java.util.stream.Collectors;

public class ReportService {

    private final TaskRepository taskRepository = new TaskRepository();

    public void printTaskCountByStatus() {
        System.out.println("\n=== TASKS BY STATUS ===");

        taskRepository.findAll().stream()
                .collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()))
                .forEach((k, v) -> System.out.println(k + " = " + v));
    }

    public void printTaskCountByPriority() {
        System.out.println("\n=== TASKS BY PRIORITY ===");

        taskRepository.findAll().stream()
                .collect(Collectors.groupingBy(Task::getPriority, Collectors.counting()))
                .forEach((k, v) -> System.out.println(k + " = " + v));
    }

    public void printUserActivityCount(String email) {
        System.out.println("\n=== USER ACTIVITY COUNT: " + email + " ===");

        long count = InMemoryDatabase.activities.stream()
                .filter(a -> a.toString().contains(email))
                .count();

        System.out.println("Total activities = " + count);
    }
}
