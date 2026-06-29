package com.rumortown.report;

import com.rumortown.game.GameSession;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "truth_reports")
public class TruthReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private GameSession session;

    @Column(columnDefinition = "text")
    private String eventSummary;

    @Column(length = 160)
    private String responsiblePerson;

    @Column(columnDefinition = "text")
    private String keyEvidence;

    @Column(columnDefinition = "text")
    private String ruleExplanation;

    @Column(columnDefinition = "text")
    private String npcLies;

    @Column(columnDefinition = "text")
    private String conclusion;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected TruthReport() {
    }

    private TruthReport(GameSession session, SubmitTruthReportRequest request, LocalDateTime createdAt) {
        this.session = session;
        this.eventSummary = request.eventSummary();
        this.responsiblePerson = request.responsiblePerson();
        this.keyEvidence = request.keyEvidence();
        this.ruleExplanation = request.ruleExplanation();
        this.npcLies = request.npcLies();
        this.conclusion = request.conclusion();
        this.createdAt = createdAt;
    }

    public static TruthReport submit(GameSession session, SubmitTruthReportRequest request, LocalDateTime createdAt) {
        return new TruthReport(session, request, createdAt);
    }

    public Long getId() {
        return id;
    }

    public GameSession getSession() {
        return session;
    }

    public String getEventSummary() {
        return eventSummary;
    }

    public String getResponsiblePerson() {
        return responsiblePerson;
    }

    public String getKeyEvidence() {
        return keyEvidence;
    }

    public String getRuleExplanation() {
        return ruleExplanation;
    }

    public String getNpcLies() {
        return npcLies;
    }

    public String getConclusion() {
        return conclusion;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}