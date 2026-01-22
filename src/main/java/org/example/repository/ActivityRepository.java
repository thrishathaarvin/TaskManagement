package org.example.repository;

import org.example.domain.activity.*;
import org.example.domain.task.Task;
import org.example.storage.InMemoryDatabase;
public class ActivityRepository {

    public void record(ActivityType type, Task task, String performedBy) {
        ActivityEvent event = new ActivityEvent(
                task.getId(),
                task.getVersion(),
                performedBy,
                type,
                "Version " + task.getVersion()
        );

        InMemoryDatabase.activities.add(event);
    }
}
