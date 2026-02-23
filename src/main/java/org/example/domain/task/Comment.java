package org.example.domain.task;
import org.example.domain.user.User;

import java.time.LocalDateTime;
import java.util.Objects;

public final class Comment {
    public final User author;
    public final String text;
    public final LocalDateTime timestamp;

    public Comment(User author, String text) {
        this.author = Objects.requireNonNull(author);
        this.text = Objects.requireNonNull(text);
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public String toString(){
        return "[" + timestamp + "]" + author.getName() + ": " + text;
    }

    public String getText() {
        return text;
    }
}
