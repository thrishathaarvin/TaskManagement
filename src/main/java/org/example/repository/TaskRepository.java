package org.example.repository;
import org.example.storage.InMemoryDatabase;
import org.example.domain.task.Task;
import java.util.*;

public class TaskRepository {
    public void save(Task task){
        InMemoryDatabase.tasks.put(task.getId(),task);
        InMemoryDatabase.taskHistory
                .computeIfAbsent(task.getId(),k->new ArrayList<>())
                .add(task);
    }

    public Optional<Task> findById(UUID id){
        return Optional.ofNullable(InMemoryDatabase.tasks.get(id));
    }

    public List<Task> findAll(){
        return new ArrayList<>(InMemoryDatabase.tasks.values());
    }
}
