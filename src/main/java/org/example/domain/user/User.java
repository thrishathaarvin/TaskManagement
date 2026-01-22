package org.example.domain.user;

import java.time.LocalDateTime;
import java.util.Objects;

public final class User {
    private final String name;
    private final String email;
    private final Role role;
    private final LocalDateTime createdAt;
    private final boolean active;

    private User(Builder builder){
        this.name=builder.name;
        this.email=builder.email;
        this.role=builder.role;
        this.createdAt=builder.createdAt;
        this.active=builder.active;
    }

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder{
        private String name;
        private String email;
        private Role role;
        private LocalDateTime createdAt=LocalDateTime.now();
        private boolean active=true;

        public Builder name(String name){
            this.name=Objects.requireNonNull(name);
            return this;
        }

        public Builder email(String email){
            this.email=Objects.requireNonNull(email);
            return this;
        }

        public Builder role(Role role){
            this.role=Objects.requireNonNull(role);
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt){
            this.createdAt=createdAt;
            return this;
        }

        public Builder active(boolean active){
            this.active=active;
            return this;
        }

        public User build(){
            Objects.requireNonNull(name);
            Objects.requireNonNull(email);
            Objects.requireNonNull(role);
            return new User(this);
        }

    }

    public User deactivate() {
        return User.builder()
                .name(this.name)
                .email(this.email)
                .role(this.role)
                .createdAt(this.createdAt)
                .active(false)
                .build();
    }


    public User withUpdated(String name, Role role) {
        return User.builder()
                .name(name)
                .email(this.email)
                .role(role)
                .createdAt(this.createdAt)
                .active(this.active)
                .build();
    }


    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }

    public Role getRole(){
        return role;
    }

    public LocalDateTime getCreatedAt(){
        return createdAt;
    }

    public boolean isActive(){
        return active;
    }

    @Override
    public String toString(){
        return "User{name='" + name + "', email='" + email + "', role=" + role + ", active=" + active + "}";
    }
}
