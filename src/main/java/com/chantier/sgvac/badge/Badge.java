package com.chantier.sgvac.badge;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "badges")
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String badgeCode;

    @Column(nullable = false)
    private String holderName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BadgeStatus status = BadgeStatus.ACTIVE;

    private LocalDate expiresAt;

    private LocalTime allowedStartTime;

    private LocalTime allowedEndTime;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    protected Badge() {
    }

    public Badge(String badgeCode, String holderName, BadgeStatus status,
                 LocalDate expiresAt, LocalTime allowedStartTime, LocalTime allowedEndTime) {
        this.badgeCode = badgeCode;
        this.holderName = holderName;
        this.status = status;
        this.expiresAt = expiresAt;
        this.allowedStartTime = allowedStartTime;
        this.allowedEndTime = allowedEndTime;
    }

    public Long getId() { return id; }
    public String getBadgeCode() { return badgeCode; }
    public String getHolderName() { return holderName; }
    public BadgeStatus getStatus() { return status; }
    public void setStatus(BadgeStatus status) { this.status = status; }
    public LocalDate getExpiresAt() { return expiresAt; }
    public LocalTime getAllowedStartTime() { return allowedStartTime; }
    public LocalTime getAllowedEndTime() { return allowedEndTime; }
    public Instant getCreatedAt() { return createdAt; }
}
