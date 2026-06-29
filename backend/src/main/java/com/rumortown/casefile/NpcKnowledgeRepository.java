package com.rumortown.casefile;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NpcKnowledgeRepository extends JpaRepository<NpcKnowledge, Long> {
    Optional<NpcKnowledge> findByNpcId(Long npcId);
}