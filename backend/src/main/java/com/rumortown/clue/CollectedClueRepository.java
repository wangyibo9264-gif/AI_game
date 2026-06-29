package com.rumortown.clue;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CollectedClueRepository extends JpaRepository<CollectedClue, Long> {
    long countBySessionId(Long sessionId);

    @Query("select count(cc) from CollectedClue cc where cc.session.id = :sessionId and cc.clue.critical = true")
    long countCriticalBySessionId(@Param("sessionId") Long sessionId);
}