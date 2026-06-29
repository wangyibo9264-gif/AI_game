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
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "case_npcs", uniqueConstraints = @UniqueConstraint(columnNames = {"case_id", "code"}))
public class CaseNpc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "case_id", nullable = false)
    private CaseFile caseFile;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "location_id", nullable = false)
    private CaseLocation location;

    @Column(nullable = false, length = 80)
    private String code;

    @Column(nullable = false, length = 160)
    private String name;

    @Column(length = 160)
    private String roleName;

    @Column(columnDefinition = "text")
    private String personality;

    @Column(columnDefinition = "text")
    private String speakingStyle;

    @Column(nullable = false)
    private Integer unlockStage;

    protected CaseNpc() {
    }

    public Long getId() {
        return id;
    }

    public CaseFile getCaseFile() {
        return caseFile;
    }

    public CaseLocation getLocation() {
        return location;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getPersonality() {
        return personality;
    }

    public String getSpeakingStyle() {
        return speakingStyle;
    }

    public Integer getUnlockStage() {
        return unlockStage;
    }
}