package com.chantier.sgvac.auth;

import com.chantier.sgvac.user.Role;
import com.chantier.sgvac.user.User;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class LoginAttemptServiceTest {

    private final LoginAttemptService service = new LoginAttemptService();
    private final Instant now = Instant.parse("2026-06-05T10:00:00Z");

    private User newUser() {
        return new User("agent1", "hash", Role.AGENT);
    }

    @Test
    void locksAfterThreeFailures() {
        User user = newUser();

        service.onFailure(user, now);
        service.onFailure(user, now);
        assertThat(service.isLocked(user, now)).isFalse();

        service.onFailure(user, now);
        assertThat(service.isLocked(user, now)).isTrue();
    }

    @Test
    void lockExpiresAfterDuration() {
        User user = newUser();
        service.onFailure(user, now);
        service.onFailure(user, now);
        service.onFailure(user, now);

        Instant later = now.plus(Duration.ofMinutes(16));
        assertThat(service.isLocked(user, later)).isFalse();
    }

    @Test
    void successResetsCounter() {
        User user = newUser();
        service.onFailure(user, now);
        service.onFailure(user, now);

        service.onSuccess(user);

        assertThat(user.getFailedAttempts()).isZero();
        assertThat(user.getLockUntil()).isNull();
        assertThat(service.isLocked(user, now)).isFalse();
    }
}
