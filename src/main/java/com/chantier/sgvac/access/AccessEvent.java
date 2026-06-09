package com.chantier.sgvac.access;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "access_events")
public class AccessEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private String badgeCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private AccessDecision decision;

    @Column(nullable = false, updatable = false)
    private String reason;

    @Column(nullable = false, updatable = false)
    private String checkpoint;

    @Column(nullable = false, updatable = false)
    private Instant eventTime;

    @Column(nullable = false, updatable = false)
    private String agentUsername;

    protected AccessEvent() {
    }

    public AccessEvent(String badgeCode, AccessDecision decision, String reason,
                       String checkpoint, Instant eventTime, String agentUsername) {
        this.badgeCode = badgeCode;
        this.decision = decision;
        this.reason = reason;
        this.checkpoint = checkpoint;
        this.eventTime = eventTime;
        this.agentUsername = agentUsername;
    }

    public Long getId() { return id; }
    public String getBadgeCode() { return badgeCode; }
    public AccessDecision getDecision() { return decision; }
    public String getReason() { return reason; }
    public String getCheckpoint() { return checkpoint; }
    public Instant getEventTime() { return eventTime; }
    public String getAgentUsername() { return agentUsername; }
}
