package com.chantier.sgvac.access;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AccessEventRepository extends JpaRepository<AccessEvent, Long> {
    List<AccessEvent> findAllByOrderByEventTimeDesc();
    List<AccessEvent> findByBadgeCodeOrderByEventTimeDesc(String badgeCode);
    List<AccessEvent> findByDecisionOrderByEventTimeDesc(AccessDecision decision);
}
