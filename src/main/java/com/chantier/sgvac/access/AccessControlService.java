package com.chantier.sgvac.access;

import com.chantier.sgvac.badge.Badge;
import com.chantier.sgvac.badge.BadgeRepository;
import com.chantier.sgvac.badge.BadgeStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AccessControlService {

    private final BadgeRepository badgeRepository;
    private final AccessEventRepository eventRepository;

    public AccessControlService(BadgeRepository badgeRepository,
                                AccessEventRepository eventRepository) {
        this.badgeRepository = badgeRepository;
        this.eventRepository = eventRepository;
    }

    public AccessEvent evaluate(String badgeCode, String checkpoint,
                                String agentUsername, LocalDateTime now) {
        Decision decision = decide(badgeCode, now);
        AccessEvent event = new AccessEvent(
                badgeCode, decision.outcome, decision.reason,
                checkpoint, Instant.now(), agentUsername);
        return eventRepository.save(event);
    }

    private Decision decide(String badgeCode, LocalDateTime now) {
        Optional<Badge> found = badgeRepository.findByBadgeCode(badgeCode);
        if (found.isEmpty()) {
            return new Decision(AccessDecision.DENIED, "BADGE_INCONNU");
        }
        Badge badge = found.get();

        if (badge.getStatus() == BadgeStatus.EXPIRED) {
            return new Decision(AccessDecision.DENIED, "BADGE_EXPIRE");
        }
        if (badge.getStatus() == BadgeStatus.INACTIVE) {
            return new Decision(AccessDecision.DENIED, "BADGE_INACTIF");
        }
        if (badge.getExpiresAt() != null && now.toLocalDate().isAfter(badge.getExpiresAt())) {
            return new Decision(AccessDecision.DENIED, "BADGE_EXPIRE");
        }
        if (badge.getAllowedStartTime() != null && badge.getAllowedEndTime() != null) {
            var time = now.toLocalTime();
            if (time.isBefore(badge.getAllowedStartTime())
                    || time.isAfter(badge.getAllowedEndTime())) {
                return new Decision(AccessDecision.DENIED, "HORS_PLAGE_HORAIRE");
            }
        }
        return new Decision(AccessDecision.AUTHORIZED, "OK");
    }

    private record Decision(AccessDecision outcome, String reason) {
    }
}
