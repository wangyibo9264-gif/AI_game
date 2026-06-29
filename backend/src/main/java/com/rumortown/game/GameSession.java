package com.rumortown.game;

import com.rumortown.casefile.CaseFile;
import com.rumortown.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "game_sessions")
public class GameSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "case_id", nullable = false)
    private CaseFile caseFile;

    @Column(nullable = false)
    private int currentStage = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private SessionStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected GameSession() {
    }

    private GameSession(User user, CaseFile caseFile, LocalDateTime now) {
        this.user = user;
        this.caseFile = caseFile;
        this.currentStage = 1;
        this.status = SessionStatus.ACTIVE;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public static GameSession start(User user, CaseFile caseFile, LocalDateTime now) {
        return new GameSession(user, caseFile, now);
    }

    public void advanceTo(int nextStage, LocalDateTime now) {
        this.currentStage = nextStage;
        this.updatedAt = now;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public CaseFile getCaseFile() {
        return caseFile;
    }

    public int getCurrentStage() {
        return currentStage;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}