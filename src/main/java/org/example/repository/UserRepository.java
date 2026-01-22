package org.example.repository;
import org.example.domain.user.User;
import java.util.*;
import static org.example.storage.InMemoryDatabase.users;

public class UserRepository {
    public void save(User user){
        users.put(user.getEmail(),user);
    }

    public Optional<User> findByEmail(String email){
        return Optional.ofNullable(users.get(email));
    }

    public Optional<User> findByName(String name) {
        return users.values().stream()
                .filter(u -> u.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public void deactivateByEmail(String email) {
        findByEmail(email).ifPresent(user ->
                users.put(email.toLowerCase(), user.deactivate())
        );
    }

    public List<User> findAll(){
        return new ArrayList<>(users.values());
    }

}
