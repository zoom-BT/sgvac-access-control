package com.chantier.sgvac.access;

import com.chantier.sgvac.badge.Badge;
import com.chantier.sgvac.badge.BadgeRepository;
import com.chantier.sgvac.badge.BadgeStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccessControlServiceTest {

    private BadgeRepository badgeRepository;
    private AccessEventRepository eventRepository;
    private AccessControlService service;

    private static final LocalDateTime NOON = LocalDateTime.of(2026, 6, 5, 12, 0);

    @BeforeEach
    void setUp() {
        badgeRepository = mock(BadgeRepository.class);
        eventRepository = mock(AccessEventRepository.class);
        when(eventRepository.save(any(AccessEvent.class))).thenAnswer(i -> i.getArgument(0));
        service = new AccessControlService(badgeRepository, eventRepository);
    }

    @Test
    void authorizesValidActiveBadge() {
        Badge badge = new Badge("B-001", "Jean", BadgeStatus.ACTIVE,
                LocalDate.of(2026, 12, 31), null, null);
        when(badgeRepository.findByBadgeCode("B-001")).thenReturn(Optional.of(badge));

        AccessEvent event = service.evaluate("B-001", "Portail A", "agent1", NOON);

        assertThat(event.getDecision()).isEqualTo(AccessDecision.AUTHORIZED);
        assertThat(event.getReason()).isEqualTo("OK");
        verify(eventRepository).save(any(AccessEvent.class));
    }

    @Test
    void deniesUnknownBadge() {
        when(badgeRepository.findByBadgeCode("X")).thenReturn(Optional.empty());

        AccessEvent event = service.evaluate("X", "Portail A", "agent1", NOON);

        assertThat(event.getDecision()).isEqualTo(AccessDecision.DENIED);
        assertThat(event.getReason()).isEqualTo("BADGE_INCONNU");
        verify(eventRepository).save(any(AccessEvent.class));
    }

    @Test
    void deniesInactiveBadge() {
        Badge badge = new Badge("B-002", "Marc", BadgeStatus.INACTIVE, null, null, null);
        when(badgeRepository.findByBadgeCode("B-002")).thenReturn(Optional.of(badge));

        AccessEvent event = service.evaluate("B-002", "Portail A", "agent1", NOON);

        assertThat(event.getDecision()).isEqualTo(AccessDecision.DENIED);
        assertThat(event.getReason()).isEqualTo("BADGE_INACTIF");
    }

    @Test
    void deniesExpiredByStatus() {
        Badge badge = new Badge("B-003", "Luc", BadgeStatus.EXPIRED, null, null, null);
        when(badgeRepository.findByBadgeCode("B-003")).thenReturn(Optional.of(badge));

        AccessEvent event = service.evaluate("B-003", "Portail A", "agent1", NOON);

        assertThat(event.getDecision()).isEqualTo(AccessDecision.DENIED);
        assertThat(event.getReason()).isEqualTo("BADGE_EXPIRE");
    }

    @Test
    void deniesExpiredByDate() {
        Badge badge = new Badge("B-004", "Paul", BadgeStatus.ACTIVE,
                LocalDate.of(2026, 1, 1), null, null);
        when(badgeRepository.findByBadgeCode("B-004")).thenReturn(Optional.of(badge));

        AccessEvent event = service.evaluate("B-004", "Portail A", "agent1", NOON);

        assertThat(event.getDecision()).isEqualTo(AccessDecision.DENIED);
        assertThat(event.getReason()).isEqualTo("BADGE_EXPIRE");
    }

    @Test
    void deniesOutsideAllowedHours() {
        Badge badge = new Badge("B-005", "Yves", BadgeStatus.ACTIVE,
                null, LocalTime.of(8, 0), LocalTime.of(11, 0));
        when(badgeRepository.findByBadgeCode("B-005")).thenReturn(Optional.of(badge));

        AccessEvent event = service.evaluate("B-005", "Portail A", "agent1", NOON);

        assertThat(event.getDecision()).isEqualTo(AccessDecision.DENIED);
        assertThat(event.getReason()).isEqualTo("HORS_PLAGE_HORAIRE");
    }
}
