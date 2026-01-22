package org.example.domain.activity;

import java.time.LocalDateTime;
import java.util.UUID;

public final class ActivityEvent {
    private final UUID taskId;
    private final int taskVersion;
    private final String performedBy;
    private final ActivityType type;
    private final String description;
    private final LocalDateTime timestamp;

    public ActivityEvent(UUID taskId, int taskVersion, String performedBy, ActivityType type, String description) {
        this.taskId = taskId;
        this.taskVersion = taskVersion;
        this.performedBy = performedBy;
        this.type = type;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "[" + timestamp + "] " + type + " by " + performedBy
                + " on Task " + taskId + " v" + taskVersion + " : " + description;
    }
}
