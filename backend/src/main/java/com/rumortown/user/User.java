package com.rumortown.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String displayName;

    @Column(nullable = false)
    private boolean guest = true;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected User() {
    }

    private User(String displayName, boolean guest, LocalDateTime createdAt) {
        this.displayName = displayName;
        this.guest = guest;
        this.createdAt = createdAt;
    }

    public static User guest(String displayName, LocalDateTime createdAt) {
        return new User(displayName, true, createdAt);
    }

    public Long getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isGuest() {
        return guest;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
