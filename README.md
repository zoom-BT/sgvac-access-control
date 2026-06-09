# SGVAC — Système de Gestion des Voies d'Accès de Chantier (MVP)

Application web Java/Spring Boot de contrôle et traçabilité des accès sur chantier.

## Prérequis
- Java 21 (JDK)
- Gradle Wrapper inclus (`gradlew.bat` / `gradlew`)

## Lancer en local (profil H2 par défaut)
```bash
./gradlew bootRun
```
Application : http://localhost:8080

## Comptes de démo
| Identifiant | Mot de passe | Rôle  |
|-------------|--------------|-------|
| admin       | admin123!    | ADMIN |
| agent       | agent123!    | AGENT |

## Badges de démo
| Code  | État     | Particularité            |
|-------|----------|--------------------------|
| B-001 | ACTIVE   | valide                   |
| B-002 | EXPIRED  | expiré                   |
| B-003 | ACTIVE   | horaires 08:00–11:00     |
| B-004 | INACTIVE | désactivé                |

## Tests
```bash
./gradlew test
```

## Profil PostgreSQL
```bash
./gradlew bootRun --args='--spring.profiles.active=postgres'
```
