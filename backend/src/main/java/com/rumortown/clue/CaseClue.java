package com.rumortown.clue;

import com.rumortown.casefile.CaseFile;
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

@Entity
@Table(name = "case_clues", uniqueConstraints = @UniqueConstraint(columnNames = {"case_id", "clue_code"}))
public class CaseClue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "case_id", nullable = false)
    private CaseFile caseFile;

    @Column(nullable = false, length = 80)
    private String clueCode;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private ClueCategory category;

    @Column(nullable = false)
    private Integer unlockStage;

    @Column(nullable = false)
    private boolean critical = false;

    protected CaseClue() {
    }

    public Long getId() {
        return id;
    }

    public CaseFile getCaseFile() {
        return caseFile;
    }

    public String getClueCode() {
        return clueCode;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public ClueCategory getCategory() {
        return category;
    }

    public Integer getUnlockStage() {
        return unlockStage;
    }

    public boolean isCritical() {
        return critical;
    }
}