package com.rumortown.report;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "report_scores")
public class ReportScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "report_id", nullable = false)
    private TruthReport report;

    @Column(nullable = false)
    private Integer truthScore;

    @Column(nullable = false)
    private Integer clueScore;

    @Column(nullable = false)
    private Integer ruleScore;

    @Column(length = 120)
    private String ending;

    @Column(columnDefinition = "text")
    private String summary;

    @Column(columnDefinition = "text")
    private String missedPoints;

    protected ReportScore() {
    }

    private ReportScore(TruthReport report, Integer truthScore, Integer clueScore, Integer ruleScore, String ending, String summary, String missedPoints) {
        this.report = report;
        this.truthScore = truthScore;
        this.clueScore = clueScore;
        this.ruleScore = ruleScore;
        this.ending = ending;
        this.summary = summary;
        this.missedPoints = missedPoints;
    }

    public static ReportScore of(TruthReport report, Integer truthScore, Integer clueScore, Integer ruleScore, String ending, String summary, String missedPoints) {
        return new ReportScore(report, truthScore, clueScore, ruleScore, ending, summary, missedPoints);
    }

    public Long getId() {
        return id;
    }

    public Integer getTruthScore() {
        return truthScore;
    }

    public Integer getClueScore() {
        return clueScore;
    }

    public Integer getRuleScore() {
        return ruleScore;
    }

    public String getEnding() {
        return ending;
    }

    public String getSummary() {
        return summary;
    }

    public String getMissedPoints() {
        return missedPoints;
    }
}