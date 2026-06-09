package com.chantier.sgvac.access;

import com.chantier.sgvac.badge.Badge;
import com.chantier.sgvac.badge.BadgeRepository;
import com.chantier.sgvac.badge.BadgeStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AccessControlIntegrationTest {

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private AccessEventRepository eventRepository;

    @Autowired
    private AccessControlService accessControlService;

    @Test
    void fullFlowPersistsEvent() {
        badgeRepository.save(new Badge("IT-001", "Test", BadgeStatus.ACTIVE,
                LocalDate.of(2030, 1, 1), null, null));

        long before = eventRepository.count();
        AccessEvent event = accessControlService.evaluate(
                "IT-001", "Portail Test", "agent", LocalDateTime.now());

        assertThat(event.getId()).isNotNull();
        assertThat(event.getDecision()).isEqualTo(AccessDecision.AUTHORIZED);
        assertThat(eventRepository.count()).isEqualTo(before + 1);
    }
}
