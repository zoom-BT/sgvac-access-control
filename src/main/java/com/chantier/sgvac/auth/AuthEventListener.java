package com.chantier.sgvac.auth;

import com.chantier.sgvac.user.User;
import com.chantier.sgvac.user.UserRepository;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class AuthEventListener {

    private final UserRepository userRepository;
    private final LoginAttemptService loginAttemptService;

    public AuthEventListener(UserRepository userRepository,
                             LoginAttemptService loginAttemptService) {
        this.userRepository = userRepository;
        this.loginAttemptService = loginAttemptService;
    }

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        userRepository.findByUsername(username).ifPresent(user -> {
            loginAttemptService.onSuccess(user);
            userRepository.save(user);
        });
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if (principal == null) {
            return;
        }
        Optional<User> found = userRepository.findByUsername(principal.toString());
        found.ifPresent(user -> {
            loginAttemptService.onFailure(user, Instant.now());
            userRepository.save(user);
        });
    }
}
