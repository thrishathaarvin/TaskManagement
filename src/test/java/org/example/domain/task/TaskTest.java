package org.example.domain.task;

import org.example.domain.user.Role;
import org.example.domain.user.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {

    @Test
    public void testTaskBuilder() {
        User creator = User.builder()
                .name("Alice")
                .email("alice@example.com")
                .role(Role.DEVELOPER)
                .build();

        Task task = Task.builder()
                .title("My Task")
                .description("Do something important")
                .createdBy(creator)
                .build();

        assertEquals("My Task", task.getTitle());
        assertEquals("Do something important", task.getDescription());
        assertEquals(TaskStatus.OPEN, task.getStatus());
        assertEquals(1, task.getVersion());
        assertEquals(creator, task.getCreatedBy());
        assertTrue(task.getAssignedTo().isEmpty());
        assertTrue(task.getDueDate().isEmpty());
        assertNotNull(task.getComments());
        assertNotNull(task.getTags());
    }

    @Test
    public void testTaskStatusChange() {
        User creator = User.builder()
                .name("Bob")
                .email("bob@example.com")
                .role(Role.DEVELOPER)
                .build();

        Task task = Task.builder()
                .title("Task 2")
                .description("Desc")
                .createdBy(creator)
                .build();

        Task inProgress = task.changeStatus(TaskStatus.IN_PROGRESS);
        assertEquals(TaskStatus.IN_PROGRESS, inProgress.getStatus());
        assertEquals(task.getVersion() + 1, inProgress.getVersion());

        // Invalid transition should throw
        assertThrows(IllegalStateException.class, () -> inProgress.changeStatus(TaskStatus.OPEN));
    }

    @Test
    public void testTaskWithAssigneeAndComment() {
        User creator = User.builder().name("Carol").email("c@example.com").role(Role.DEVELOPER).build();
        User assignee = User.builder().name("Dave").email("d@example.com").role(Role.DEVELOPER).build();

        Task task = Task.builder().title("Task 3").description("Desc").createdBy(creator).build();

        Task assignedTask = task.withAssignee(assignee);
        assertEquals(assignee, assignedTask.getAssignedTo().get());

        Comment comment = new Comment(creator, "Nice work");
        Task commentedTask = assignedTask.withComment(comment);
        assertEquals(1, commentedTask.getComments().size());
        assertEquals("Nice work", commentedTask.getComments().get(0).getText());
    }
}