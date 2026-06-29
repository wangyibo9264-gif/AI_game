package com.rumortown.clue;

import com.rumortown.game.GameSession;
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
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

@Entity
@Table(name = "collected_clues", uniqueConstraints = @UniqueConstraint(columnNames = {"session_id", "clue_id"}))
public class CollectedClue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private GameSession session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "clue_id", nullable = false)
    private CaseClue clue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private ClueImportance importance = ClueImportance.NORMAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private CollectedClueStatus status = CollectedClueStatus.UNRESOLVED;

    @Column(nullable = false)
    private LocalDateTime collectedAt;

    protected CollectedClue() {
    }
}