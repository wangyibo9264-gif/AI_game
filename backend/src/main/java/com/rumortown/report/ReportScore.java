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
}