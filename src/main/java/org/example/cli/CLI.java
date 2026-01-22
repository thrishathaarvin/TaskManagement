package org.example.cli;

import org.example.domain.activity.ActivityType;
import org.example.domain.task.Comment;
import org.example.domain.task.Priority;
import org.example.domain.task.Task;
import org.example.domain.task.TaskStatus;
import org.example.domain.user.Role;
import org.example.domain.user.User;
import org.example.repository.ActivityRepository;
import org.example.repository.TaskRepository;
import org.example.repository.UserRepository;
import org.example.service.TaskService;
import org.example.report.ReportService;
import org.example.service.search.TaskSearchCriteria;
import org.example.service.search.TaskSearchService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class CLI {

    private final Scanner scanner = new Scanner(System.in);
    private final UserRepository userRepo = new UserRepository();
    private final TaskService taskService = new TaskService();
    private final TaskRepository taskRepo = new TaskRepository();
    private final TaskSearchService searchService = new TaskSearchService();
    private final ReportService reportService = new ReportService();
    private final ActivityRepository activityRepo = new ActivityRepository();


    public void start() {
        while (true) {
            System.out.println("\n=== TASK MANAGEMENT SYSTEM ===");
            System.out.println("1. User Management");
            System.out.println("2. Task Management");
            System.out.println("3. Search & Filter");
            System.out.println("4. Reports");
            System.out.println("5. Exit");
            System.out.print("Choice: ");

            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1 -> userMenu();
                case 2 -> taskMenu();
                case 3 -> searchMenu();
                case 4 -> reportMenu();
                case 5 -> {
                    System.out.println("Bye!");
                    return;
                }
                default -> System.out.println("Invalid choice");
            }
        }
    }

    private void userMenu() {
        System.out.println("1. Create User");
        System.out.println("2. View User");
        System.out.println("3. List Users");
        System.out.println("4. Update User");
        System.out.println("5. Delete User");

        int choice = Integer.parseInt(scanner.nextLine());

        switch (choice) {
            case 1 -> createUser();
            case 2 -> viewUser();
            case 3 -> listUsers();
            case 4 -> updateUser();
            case 5 -> deleteUser();
            default -> System.out.println("Invalid choice");
        }
    }
    private void createUser() {
        System.out.print("Name: ");
        String name = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Role (DEVELOPER, MANAGER, ADMIN): ");
        Role role = Role.valueOf(scanner.nextLine().toUpperCase());

        if (userRepo.findByEmail(email).isPresent()) {
            System.out.println("Email already exists!");
            return;
        }

        User user = User.builder()
                .name(name)
                .email(email)
                .role(role)
                .build();

        userRepo.save(user);
        System.out.println("User created: " + user.getName());
    }

    private void viewUser() {
        System.out.print("Enter user mail: ");
        try {
            String email= scanner.nextLine().trim();
            Optional<User> user = userRepo.findByEmail(email);

            if (user.isEmpty()) {
                System.out.println("User not found.");
            } else {
                System.out.println(user.get());
            }
        } catch (Exception e) {
            System.out.println("Invalid email format.");
        }
    }

    private void listUsers() {
        System.out.println("Sort by: 1. Name  2. Email  3. Role");
        int c = Integer.parseInt(scanner.nextLine());

        Comparator<User> comp = switch (c) {
            case 1 -> Comparator.comparing(User::getName);
            case 2 -> Comparator.comparing(User::getEmail);
            case 3 -> Comparator.comparing(User::getRole);
            default -> null;
        };

        List<User> users = userRepo.findAll();

        if (comp != null) users = users.stream().sorted(comp).toList();

        System.out.println("Total users: " + users.size());
        users.forEach(System.out::println);
    }

    private void updateUser() {
        System.out.print("Enter user mail: ");
        String mailInput = scanner.nextLine().trim();

        Optional<User> optionalUser = userRepo.findByEmail(mailInput);

        if (optionalUser.isEmpty()) {
            System.out.println("User not found.");
            return;
        }

        User user = optionalUser.get();

        System.out.print("New name (blank to skip): ");
        String newNameInput = scanner.nextLine();

        System.out.print("New role (DEVELOPER, MANAGER, ADMIN) or blank: ");
        String roleStr = scanner.nextLine();

        String newName = newNameInput.isBlank() ? user.getName() : newNameInput;
        Role newRole = roleStr.isBlank()
                ? user.getRole()
                : Role.valueOf(roleStr.toUpperCase());

        User updated = user.withUpdated(newName, newRole);
        userRepo.save(updated);

        System.out.println("User updated.");
    }

    private void deleteUser() {
        System.out.print("Enter user email: ");
        String email = scanner.nextLine();

        Optional<User> optionalUser = userRepo.findByEmail(email);

        if (optionalUser.isEmpty()) {
            System.out.println("User not found.");
            return;
        }

        boolean hasActiveTasks = taskRepo.findAll().stream()
                .anyMatch(t ->
                        t.getAssignedTo().isPresent() &&
                                t.getAssignedTo().get().getEmail().equalsIgnoreCase(email) &&
                                t.getStatus() != TaskStatus.COMPLETED &&
                                t.getStatus() != TaskStatus.CANCELLED
                );

        if (hasActiveTasks) {
            System.out.println("Cannot delete user: user has active tasks.");
            return;
        }

        userRepo.deactivateByEmail(email);
        System.out.println("User deactivated successfully.");
    }

    private void taskMenu() {
        System.out.println("\n=== TASK MANAGEMENT ===");
        System.out.println("1. Create Task");
        System.out.println("2. View Task");
        System.out.println("3. Update Task Status");
        System.out.println("4. Assign / Reassign Task");
        System.out.println("5. Update Priority");
        System.out.println("6. Update Due Date");
        System.out.println("7. Add Comment");
        //System.out.println("8. View Task History");

        int choice = Integer.parseInt(scanner.nextLine());

        switch (choice) {
            case 1 -> createTask();
            case 2 -> viewTask();
            case 3 -> updateTaskStatus();
            case 4 -> assignTask();
            case 5 -> updatePriority();
            case 6 -> updateDueDate();
            case 7 -> addComment();
            //case 8 -> viewTaskHistory();
            default -> System.out.println("Invalid option");
        }
    }

    private void createTask() {
        System.out.print("Title: ");
        String title = scanner.nextLine();

        System.out.print("Description: ");
        String description = scanner.nextLine();

        System.out.print("Creator Email: ");
        String creatorEmail = scanner.nextLine();

        Optional<User> creatorOpt = userRepo.findByEmail(creatorEmail);
        if (creatorOpt.isEmpty()) {
            System.out.println("Creator not found.");
            return;
        }

        User creator = creatorOpt.get();

        Task task = Task.builder()
                .title(title)
                .description(description)
                .createdBy(creator)
                .status(TaskStatus.OPEN)
                .priority(Priority.MEDIUM)
                .version(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        taskRepo.save(task);
        activityRepo.record(ActivityType.TASK_CREATED, task, creator.getEmail());

        System.out.println("Task created successfully.");
        System.out.println("Task ID: " + task.getId());
    }

    private void viewTask() {
        System.out.print("Enter task ID: ");
        UUID id = UUID.fromString(scanner.nextLine());

        taskRepo.findById(id).ifPresentOrElse(task -> {
            System.out.println(task);
            System.out.println("Assignee: " +
                    (task.getAssignedTo().isEmpty() ? "Unassigned" : task.getAssignedTo().get().getEmail()));
            System.out.println("Due Date: " +
                    (task.getDueDate().isEmpty() ? "No deadline" : task.getDueDate()));

            task.getComments().forEach(System.out::println);
        }, () -> System.out.println("Task not found"));
    }

    private void updateTaskStatus() {
        System.out.print("Enter Task ID: ");
        UUID id = UUID.fromString(scanner.nextLine());

        Optional<Task> optTask = taskRepo.findById(id);
        if (optTask.isEmpty()) {
            System.out.println("Task not found.");
            return;
        }

        Task task = optTask.get();

        System.out.print("New Status (OPEN, IN_PROGRESS, COMPLETED, CANCELLED): ");
        TaskStatus newStatus = TaskStatus.valueOf(scanner.nextLine().toUpperCase());

        if (!task.getStatus().canTransitionTo(newStatus)) {
            System.out.println("Invalid status transition.");
            return;
        }

        Task updated = task.changeStatus(newStatus);

        taskRepo.save(updated);

        activityRepo.record(ActivityType.STATUS_CHANGED, updated, "SYSTEM");

        System.out.println("Task status updated.");
    }

    private void assignTask() {
        System.out.print("Enter Task ID: ");
        UUID id = UUID.fromString(scanner.nextLine());

        Optional<Task> optTask = taskRepo.findById(id);
        if (optTask.isEmpty()) {
            System.out.println("Task not found.");
            return;
        }

        Task task = optTask.get();

        System.out.print("Assignee Email (blank to unassign): ");
        String email = scanner.nextLine();
        User assignee = email.isBlank() ? null : userRepo.findByEmail(email).orElse(null);

        Task updated = task.withAssignee(assignee);

        taskRepo.save(updated);
        activityRepo.record(ActivityType.ASSIGNEE_CHANGED, updated, "SYSTEM");

        System.out.println("Task assignee updated.");
    }

    private void updatePriority() {
        System.out.print("Enter Task ID: ");
        UUID id = UUID.fromString(scanner.nextLine());

        Optional<Task> optTask = taskRepo.findById(id);
        if (optTask.isEmpty()) {
            System.out.println("Task not found.");
            return;
        }

        Task task = optTask.get();

        System.out.print("New Priority (LOW, MEDIUM, HIGH, CRITICAL): ");
        Priority priority = Priority.valueOf(scanner.nextLine().toUpperCase());

        Task updated = task.withPriority(priority);

        taskRepo.save(updated);
        activityRepo.record(ActivityType.PRIORITY_CHANGED, updated, "SYSTEM");

        System.out.println("Task priority updated.");
    }

    private void updateDueDate() {
        System.out.print("Enter Task ID: ");
        UUID id = UUID.fromString(scanner.nextLine());

        Optional<Task> optTask = taskRepo.findById(id);
        if (optTask.isEmpty()) {
            System.out.println("Task not found.");
            return;
        }

        Task task = optTask.get();

        System.out.print("New Due Date (yyyy-MM-dd) or blank to remove: ");
        String dueStr = scanner.nextLine();
        LocalDateTime due = dueStr.isBlank() ? null : LocalDateTime.parse(dueStr);

        Task updated = task.withDueDate(due);

        taskRepo.save(updated);
        activityRepo.record(ActivityType.DUE_DATE_CHANGED, updated, "SYSTEM");

        System.out.println("Task due date updated.");
    }

    private void addComment() {
        System.out.print("Enter Task ID: ");
        UUID id = UUID.fromString(scanner.nextLine());

        Optional<Task> optTask = taskRepo.findById(id);
        if (optTask.isEmpty()) {
            System.out.println("Task not found.");
            return;
        }

        Task task = optTask.get();

        System.out.print("Comment: ");
        String text = scanner.nextLine();

        System.out.print("Author Email: ");
        String authorEmail = scanner.nextLine();
        User author = userRepo.findByEmail(authorEmail).orElse(null);

        if (author == null) {
            System.out.println("User not found. Cannot add comment.");
            return;
        }

        Comment comment = new Comment(author, text);

        Task updated = task.withComment(comment);
        taskRepo.save(updated);
        activityRepo.record(ActivityType.COMMENT_ADDED, updated, author.getEmail());

        System.out.println("Comment added.");
    }

    private void searchMenu() {
        System.out.println("\n=== SEARCH TASKS ===");
        System.out.println("1. Find by Status");
        System.out.println("2. Find by Priority");
        System.out.println("3. Find by Assignee Email");
        System.out.println("4. Find by Creator Email");
        System.out.println("5. Find Overdue Tasks");
        System.out.println("6. Find by Tag");


        int choice = Integer.parseInt(scanner.nextLine());

        List<Task> results;

        switch (choice) {
            case 1 -> {
                System.out.print("Status: ");
                TaskStatus status = TaskStatus.valueOf(scanner.nextLine().toUpperCase());
                results = searchService.findByStatus(status);
            }
            case 2 -> {
                System.out.print("Priority: ");
                Priority priority = Priority.valueOf(scanner.nextLine().toUpperCase());
                results = searchService.findByPriority(priority);
            }
            case 3 -> {
                System.out.print("Assignee email: ");
                String email = scanner.nextLine();
                results = searchService.findByAssigneeEmail(email);
            }
            case 4 -> {
                System.out.print("Creator email: ");
                String email = scanner.nextLine();
                results = searchService.findByCreatorEmail(email);
            }
            case 5 -> {
                results = searchService.findOverdue();
            }
            case 6 -> {
                System.out.print("Tag: ");
                String tag = scanner.nextLine();
                results = searchService.findByTag(tag);
            }
            default -> {
                System.out.println("Invalid choice");
                return;
            }
        }

        if (results.isEmpty()) {
            System.out.println("No tasks found.");
        } else {
            System.out.println("\n=== RESULTS (" + results.size() + ") ===");
            results.forEach(System.out::println);
        }
    }

    private void reportMenu() {
        System.out.println("\n1. Tasks by Status");
        System.out.println("2. Tasks by Priority");
        System.out.println("3. User Activity Count");

        int choice = Integer.parseInt(scanner.nextLine());

        if (choice == 1) reportService.printTaskCountByStatus();
        if (choice == 2) reportService.printTaskCountByPriority();
        if (choice == 3) {
            System.out.print("Email: ");
            reportService.printUserActivityCount(scanner.nextLine());
        }
    }


}
