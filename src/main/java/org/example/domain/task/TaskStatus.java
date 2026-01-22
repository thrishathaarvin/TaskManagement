package org.example.domain.task;

import java.util.EnumSet;
import java.util.Set;

public enum TaskStatus {
    OPEN,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED;

    private Set<TaskStatus> allowedNext;

    static{
        OPEN.allowedNext=EnumSet.of(IN_PROGRESS, CANCELLED);
        IN_PROGRESS.allowedNext=EnumSet.of(COMPLETED, CANCELLED);
        COMPLETED.allowedNext=EnumSet.noneOf(TaskStatus.class);
        CANCELLED.allowedNext=EnumSet.of(OPEN);
    }

    public boolean canTransitionTo(TaskStatus next){
        return allowedNext.contains(next);
    }
}
