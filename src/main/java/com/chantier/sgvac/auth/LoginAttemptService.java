package com.chantier.sgvac.auth;

import com.chantier.sgvac.user.User;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class LoginAttemptService {

    public static final int MAX_ATTEMPTS = 3;
    public static final Duration LOCK_DURATION = Duration.ofMinutes(15);

    public boolean isLocked(User user, Instant now) {
        Instant lockUntil = user.getLockUntil();
        return lockUntil != null && now.isBefore(lockUntil);
    }

    public void onFailure(User user, Instant now) {
        user.setFailedAttempts(user.getFailedAttempts() + 1);
        if (user.getFailedAttempts() >= MAX_ATTEMPTS) {
            user.setLockUntil(now.plus(LOCK_DURATION));
        }
    }

    public void onSuccess(User user) {
        user.setFailedAttempts(0);
        user.setLockUntil(null);
    }
}
