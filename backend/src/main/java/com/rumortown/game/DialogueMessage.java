package com.rumortown.game;

import com.rumortown.casefile.CaseNpc;
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
@Table(name = "dialogue_messages")
public class DialogueMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private GameSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "npc_id")
    private CaseNpc npc;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private DialogueSenderType sender;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    @Column(nullable = false)
    private Integer stage;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected DialogueMessage() {
    }

    private DialogueMessage(GameSession session, CaseNpc npc, DialogueSenderType sender, String content, Integer stage, LocalDateTime createdAt) {
        this.session = session;
        this.npc = npc;
        this.sender = sender;
        this.content = content;
        this.stage = stage;
        this.createdAt = createdAt;
    }

    public static DialogueMessage of(GameSession session, CaseNpc npc, DialogueSenderType sender, String content, LocalDateTime createdAt) {
        return new DialogueMessage(session, npc, sender, content, session.getCurrentStage(), createdAt);
    }

    public Long getId() {
        return id;
    }

    public GameSession getSession() {
        return session;
    }

    public CaseNpc getNpc() {
        return npc;
    }

    public DialogueSenderType getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public Integer getStage() {
        return stage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}