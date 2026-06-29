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
@Table(name = "npc_knowledge")
public class NpcKnowledge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "npc_id", nullable = false)
    private CaseNpc npc;

    @Column(columnDefinition = "text")
    private String knownFacts;

    @Column(columnDefinition = "text")
    private String revealableClueCodes;

    @Column(columnDefinition = "text")
    private String hiddenFacts;

    @Column(columnDefinition = "text")
    private String forbiddenTopics;

    protected NpcKnowledge() {
    }

    public Long getId() {
        return id;
    }

    public CaseNpc getNpc() {
        return npc;
    }

    public String getKnownFacts() {
        return knownFacts;
    }

    public String getRevealableClueCodes() {
        return revealableClueCodes;
    }

    public String getHiddenFacts() {
        return hiddenFacts;
    }

    public String getForbiddenTopics() {
        return forbiddenTopics;
    }
}