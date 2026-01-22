package org.example.storage;
import org.example.domain.activity.ActivityEvent;
import org.example.domain.task.Task;
import org.example.domain.user.User;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryDatabase {
    public static final Map<String, User> users=new ConcurrentHashMap<>();
    public static final Map<UUID, Task> tasks=new ConcurrentHashMap<>();
    public static final Map<UUID, List<Task>> taskHistory=new ConcurrentHashMap<>();
    public static final List<ActivityEvent> activities=Collections.synchronizedList(new ArrayList<>());
}
