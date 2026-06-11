package com.chantier.sgvac.auth;

import com.chantier.sgvac.api.dto.LoginRequest;
import com.chantier.sgvac.api.dto.LoginResponse;
import com.chantier.sgvac.user.User;
import com.chantier.sgvac.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Service
public class ApiAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;
    private final JwtService jwtService;

    public ApiAuthService(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          LoginAttemptService loginAttemptService,
                          JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants invalides"));

        Instant now = Instant.now();
        if (loginAttemptService.isLocked(user, now)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Compte verrouillé");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            loginAttemptService.onFailure(user, now);
            userRepository.save(user);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants invalides");
        }

        loginAttemptService.onSuccess(user);
        userRepository.save(user);

        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());
        return new LoginResponse(token, user.getUsername(), user.getRole().name());
    }
}
