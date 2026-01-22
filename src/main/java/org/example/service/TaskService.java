package org.example.service;
import org.example.domain.activity.*;
import org.example.domain.task.*;
import org.example.repository.TaskRepository;

import java.util.List;
import java.util.UUID;

public class TaskService {
    private final TaskRepository taskRepo=new TaskRepository();

//    public List<Task> getTaskHistory(UUID taskId){
//        return taskRepo.findHistory(taskId);
//    }
}
