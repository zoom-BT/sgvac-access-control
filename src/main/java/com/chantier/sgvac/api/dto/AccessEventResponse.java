package com.chantier.sgvac.api.dto;

import com.chantier.sgvac.access.AccessDecision;
import com.chantier.sgvac.access.AccessEvent;

import java.time.Instant;

public record AccessEventResponse(
        Long id,
        String badgeCode,
        AccessDecision decision,
        String reason,
        String checkpoint,
        Instant eventTime,
        String agentUsername
) {
    public static AccessEventResponse from(AccessEvent event) {
        return new AccessEventResponse(
                event.getId(),
                event.getBadgeCode(),
                event.getDecision(),
                event.getReason(),
                event.getCheckpoint(),
                event.getEventTime(),
                event.getAgentUsername()
        );
    }
}
