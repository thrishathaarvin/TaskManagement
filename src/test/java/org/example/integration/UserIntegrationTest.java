package org.example.integration;

import org.example.domain.user.Role;
import org.example.domain.user.User;
import org.example.domain.task.Task;
import org.example.domain.task.TaskStatus;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserIntegrationTest {

    @Test
    public void testUserCreationAndDeactivation() {
        User alice = User.builder()
                .name("Alice")
                .email("alice@example.com")
                .role(Role.DEVELOPER)
                .build();

        assertEquals("Alice", alice.getName());
        assertEquals(Role.DEVELOPER, alice.getRole());
        assertTrue(alice.isActive());

        User deactivatedAlice = alice.deactivate();
        assertFalse(deactivatedAlice.isActive());
        assertEquals(alice.getName(), deactivatedAlice.getName());
        assertEquals(alice.getRole(), deactivatedAlice.getRole());
    }

    @Test
    public void testUserUpdate() {
        User bob = User.builder()
                .name("Bob")
                .email("bob@example.com")
                .role(Role.DEVELOPER)
                .build();

        User updatedBob = bob.withUpdated("Bobby", Role.ADMIN);
        assertEquals("Bobby", updatedBob.getName());
        assertEquals(Role.ADMIN, updatedBob.getRole());
        assertEquals(bob.getEmail(), updatedBob.getEmail());
        assertTrue(updatedBob.isActive());
    }

    @Test
    public void testAssignTaskToUser() {
        User creator = User.builder()
                .name("Creator")
                .email("creator@example.com")
                .role(Role.DEVELOPER)
                .build();

        User assignee = User.builder()
                .name("Assignee")
                .email("assignee@example.com")
                .role(Role.DEVELOPER)
                .build();

        Task task = Task.builder()
                .title("Integration Task")
                .description("Task for integration test")
                .createdBy(creator)
                .build();

        // Assign user to task
        Task assignedTask = task.withAssignee(assignee);

        Optional<User> assignedTo = assignedTask.getAssignedTo();
        assertTrue(assignedTo.isPresent());
        assertEquals("Assignee", assignedTo.get().getName());

        // Change status to IN_PROGRESS
        Task inProgress = assignedTask.changeStatus(TaskStatus.IN_PROGRESS);
        assertEquals(TaskStatus.IN_PROGRESS, inProgress.getStatus());
    }
}