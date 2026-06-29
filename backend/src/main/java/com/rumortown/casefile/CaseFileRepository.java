package com.rumortown.casefile;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaseFileRepository extends JpaRepository<CaseFile, Long> {
    List<CaseFile> findAllByOrderByIdAsc();
}
