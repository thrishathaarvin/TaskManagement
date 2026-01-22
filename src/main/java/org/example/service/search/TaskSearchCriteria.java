package org.example.service.search;

import org.example.domain.task.Priority;
import org.example.domain.task.TaskStatus;
import java.time.LocalDate;

    public class TaskSearchCriteria {

        public TaskStatus status;
        public Priority priority;
        public String assigneeEmail;
        public String creatorEmail;
        public String tag;
        public Boolean overdue;

        public LocalDate createdFrom;
        public LocalDate createdTo;

    }

