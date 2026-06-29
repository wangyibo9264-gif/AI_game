package com.rumortown.casefile;

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
@Table(name = "case_rules")
public class CaseRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "case_id", nullable = false)
    private CaseFile caseFile;

    @Column(nullable = false, length = 80)
    private String ruleCode;

    @Column(nullable = false, columnDefinition = "text")
    private String ruleText;

    @Column(columnDefinition = "text")
    private String truthMeaning;

    @Column(nullable = false)
    private Integer displayOrder;

    protected CaseRule() {
    }

    public Long getId() {
        return id;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public String getRuleText() {
        return ruleText;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }
}
