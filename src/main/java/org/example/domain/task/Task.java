package org.example.domain.task;

import org.example.domain.user.User;
import java.util.function.Consumer;
import java.time.LocalDateTime;
import java.util.*;

public final class Task {
    private final UUID id;
    private final int version;
    private final String title;
    private final String description;
    private final TaskStatus status;
    private final Priority priority;
    private final User createdBy;
    private final Optional<User> assignedTo;
    private final Optional<LocalDateTime> dueDate;
    private final Set<String> tags;
    private final List<Comment> comments;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public UUID getId() { return id; }
    public int getVersion() { return version; }
    public TaskStatus getStatus() { return status; }
    public Priority getPriority() { return priority; }
    public User getCreatedBy() { return createdBy; }
    public Optional<User> getAssignedTo() { return assignedTo; }
    public Optional<LocalDateTime> getDueDate() { return dueDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<Comment> getComments() { return comments; }
    public Set<String> getTags() { return tags; }


    public Task withAssignee(User assignee) {
        return copy(b -> b.assignedTo = assignee);
    }

    public Task withPriority(Priority priority) {
        return copy(b -> b.priority = priority);
    }

    public Task withDueDate(LocalDateTime dueDate) {
        return copy(b -> b.dueDate = dueDate);
    }

    public Task withComment(Comment comment) {
        return copy(b -> b.comments.add(comment));
    }

    public Task changeStatus(TaskStatus newStatus) {
        if (!this.status.canTransitionTo(newStatus)) {
            throw new IllegalStateException("Invalid status transition: " + status + " -> " + newStatus);
        }
        return copy(b -> b.status = newStatus);
    }

    private Task copy(Consumer<Builder> mutator) {
        Builder b = toBuilder();
        b.version = this.version + 1;
        b.updatedAt = LocalDateTime.now();
        mutator.accept(b);
        return b.build();
    }


    private Task(Builder b) {
        this.id = b.id;
        this.version = b.version;
        this.title = b.title;
        this.description = b.description;
        this.status = b.status;
        this.priority = b.priority;
        this.createdBy = b.createdBy;
        this.assignedTo = Optional.ofNullable(b.assignedTo);
        this.dueDate = Optional.ofNullable(b.dueDate);
        this.tags = Set.copyOf(b.tags);
        this.comments = List.copyOf(b.comments);
        this.createdAt = b.createdAt;
        this.updatedAt = b.updatedAt;
    }

    public static Builder builder() { return new Builder(); }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public static class Builder {
        private UUID id = UUID.randomUUID();
        private int version = 1;
        private String title;
        private String description;
        private TaskStatus status = TaskStatus.OPEN;
        private Priority priority = Priority.MEDIUM;
        private User createdBy;
        private User assignedTo;
        private LocalDateTime dueDate;
        private Set<String> tags = new HashSet<>();
        private List<Comment> comments = new ArrayList<>();
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime updatedAt = LocalDateTime.now();

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder title(String t) { this.title = t; return this; }
        public Builder description(String d) { this.description = d; return this; }
        public Builder status(TaskStatus s) { this.status = s; return this; }
        public Builder priority(Priority p) { this.priority = p; return this; }
        public Builder createdBy(User u) { this.createdBy = u; return this; }
        public Builder assignedTo(User u) { this.assignedTo = u; return this; }
        public Builder dueDate(LocalDateTime d) { this.dueDate = d; return this; }
        public Builder version(int v) { this.version = v; return this; }
        public Builder createdAt(LocalDateTime c) { this.createdAt = c; return this; }
        public Builder updatedAt(LocalDateTime u) { this.updatedAt = u; return this; }

        public Task build() {
            Objects.requireNonNull(title);
            Objects.requireNonNull(description);
            Objects.requireNonNull(createdBy);
            return new Task(this);
        }
    }

    private Builder toBuilder() {
        Builder b = new Builder();
        b.id(this.id);
        b.version(this.version);
        b.title(this.title);
        b.description(this.description);
        b.status(this.status);
        b.priority(this.priority);
        b.createdBy(this.createdBy);
        b.assignedTo(this.assignedTo.orElse(null));
        b.dueDate(this.dueDate.orElse(null));
        b.tags = new HashSet<>(this.tags);
        b.comments = new ArrayList<>(this.comments);
        b.createdAt(this.createdAt);
        b.updatedAt(this.updatedAt);
        return b;
    }

    @Override
    public String toString() {
        return "Task{id=" + id + ", v=" + version + ", status=" + status + ", title='" + title + "'}";
    }
}
