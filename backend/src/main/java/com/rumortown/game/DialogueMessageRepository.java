package com.rumortown.game;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DialogueMessageRepository extends JpaRepository<DialogueMessage, Long> {
}