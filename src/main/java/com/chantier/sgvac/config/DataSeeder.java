package com.chantier.sgvac.config;

import com.chantier.sgvac.badge.Badge;
import com.chantier.sgvac.badge.BadgeRepository;
import com.chantier.sgvac.badge.BadgeStatus;
import com.chantier.sgvac.user.Role;
import com.chantier.sgvac.user.User;
import com.chantier.sgvac.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BadgeRepository badgeRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, BadgeRepository badgeRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.badgeRepository = badgeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            userRepository.save(new User("admin",
                    passwordEncoder.encode("admin123!"), Role.ADMIN));
            userRepository.save(new User("agent",
                    passwordEncoder.encode("agent123!"), Role.AGENT));
        }
        if (badgeRepository.count() == 0) {
            badgeRepository.save(new Badge("B-001", "Jean Dupont (ouvrier)",
                    BadgeStatus.ACTIVE, LocalDate.of(2026, 12, 31), null, null));
            badgeRepository.save(new Badge("B-002", "Marc Petit (visiteur)",
                    BadgeStatus.EXPIRED, LocalDate.of(2026, 1, 1), null, null));
            badgeRepository.save(new Badge("B-003", "Luc Martin (sous-traitant)",
                    BadgeStatus.ACTIVE, LocalDate.of(2026, 12, 31),
                    LocalTime.of(8, 0), LocalTime.of(11, 0)));
            badgeRepository.save(new Badge("B-004", "Paul Bernard (désactivé)",
                    BadgeStatus.INACTIVE, null, null, null));
        }
    }
}
