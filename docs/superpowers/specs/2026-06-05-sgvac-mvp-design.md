²# SGVAC — Conception du MVP (V1)

> Système de Gestion des Voies d'Accès de Chantier
> Document de conception — 2026-06-05
> Statut : validé pour rédaction du plan d'implémentation

## 1. Contexte et objectif

Le SGVAC est une application destinée à sécuriser et tracer les accès sur un chantier de
construction (authentification des opérateurs, contrôle des badges, journalisation des
mouvements). Le cahier des charges complet couvre 6 modules. Ce document décrit
uniquement le **MVP (V1)**, centré sur le **noyau métier** afin de livrer rapidement une
démo fonctionnelle (objectif : soutenance).

### Objectif de réussite V1

Avoir une **démo fonctionnelle rapide** illustrant le cycle complet :
connexion → contrôle d'accès (badge simulé) → décision → journal immuable.

## 2. Périmètre

### Inclus (V1)

- Authentification (login/logout, rôles, verrouillage après 3 échecs)
- Contrôle d'accès avec **badge simulé** (saisie/scan d'un ID badge)
- Journal d'accès **immuable** + consultation/recherche simple

### Hors périmètre (itérations futures)

- Gestion avancée des badges (cycle de vie complet, association historisée)
- Alertes e-mail, notifications temps réel avancées
- Rapports PDF (quotidien/mensuel)
- Multi-rôles étendus (HSE, chef de chantier, etc.)
- Intégration matériel réel (lecteurs RFID/QR, tourniquets, anti-passback)

## 3. Stack technique

Choisie en fonction des outils réellement installés sur la machine de développement.

| Élément | Choix | Note |
|---|---|---|
| Langage | Java 21 LTS | OpenJDK 21.0.11 détecté |
| Framework | Spring Boot 3.x | Monolithe web |
| UI | Thymeleaf (server-side) | Rapidité de livraison |
| Sécurité | Spring Security | RBAC + BCrypt + CSRF |
| Persistance | Spring Data JPA + Hibernate | Abstraction multi-SGBD |
| Base de données | PostgreSQL | À démarrer (psql non installé localement) |
| Build | Gradle Wrapper (`gradlew`) | Gradle non installé globalement |
| Tests | JUnit 5 | Unitaires + intégration |

> Remarque : Docker CLI est présent mais le daemon n'était pas démarré ; PostgreSQL
> pourra être lancé via Docker une fois le daemon actif, ou installé localement.

## 4. Architecture

Application web **monolithique** Spring Boot, structurée en couches
`controller / service / repository` pour permettre une migration ultérieure vers une
architecture API + front séparé sans réécrire le métier.

### Composants

- **Auth** : login/logout, gestion de session, verrouillage temporaire après 3 échecs.
- **AccessControl** : reçoit un ID badge simulé, vérifie statut (actif/expiré),
  plage horaire et règles, renvoie une décision.
- **AuditLog** : enregistre chaque tentative (autorisé/refusé), horodatage, point de
  contrôle, motif. Lecture seule après insertion.
- **AdminConsole** (léger en V1) : consultation des logs, recherche par badge/date/statut.

## 5. Flux de données

### Flux nominal (accès autorisé)

1. Agent connecté saisit/scanne un ID badge simulé.
2. `AccessControl` valide les règles (actif, non expiré, dans la plage horaire).
3. Résultat `AUTORISÉ` affiché immédiatement.
4. `AuditLog` persiste l'événement.

### Flux refusé

1. Badge invalide / expiré / hors horaire.
2. Résultat `REFUSÉ` + motif affiché.
3. Événement journalisé obligatoirement (règle immuable).

## 6. Modèle de données

### `users`

`id`, `username`, `password_hash`, `role`, `is_locked`, `failed_attempts`,
`lock_until`, `created_at`.
Rôles V1 : `ADMIN`, `AGENT`.

### `badges`

`id`, `badge_code` (unique), `holder_name`, `status` (`ACTIVE`/`INACTIVE`/`EXPIRED`),
`expires_at`, `allowed_start_time`, `allowed_end_time`, `created_at`.

### `access_events` (journal immuable)

`id`, `badge_code`, `decision` (`AUTHORIZED`/`DENIED`), `reason`, `checkpoint`,
`event_time`, `agent_username`.
Règle : aucune mise à jour/suppression applicative après insertion.

## 7. RBAC minimal

- **ADMIN** : gère utilisateurs, badges, consultation complète des logs.
- **AGENT** : réalise les contrôles d'accès + consultation des logs opérationnels.
- Aucun autre rôle en V1.

## 8. Règles métier (MVP)

- **RM-01** : Badge non actif / expiré / hors plage horaire ⇒ `DENIED`.
- **RM-02** : Toute tentative d'accès ⇒ une entrée dans `access_events`.
- **RM-03** : 3 échecs de connexion ⇒ verrouillage temporaire du compte.
- **RM-04** : Le journal d'accès est immuable (ni modification ni suppression).
- **RM-05** : Toute connexion (succès/échec) est journalisée.

## 9. Sécurité

- Mots de passe hashés avec **BCrypt**.
- Sessions sécurisées (`HttpOnly`, expiration).
- Contrôle d'accès par rôle sur les routes (`/admin/**`, `/agent/**`).
- Protection **CSRF** activée pour les formulaires.

## 10. Gestion d'erreurs

- Messages clairs côté agent : `Badge inconnu`, `Badge expiré`, `Hors plage horaire`,
  `Compte verrouillé`.
- Erreurs techniques conservées côté logs serveur (non exposées brut).
- Fallback : si le service badge est indisponible ⇒ décision `DENIED` +
  raison `SYSTEM_UNAVAILABLE`.

## 11. Tests

- **Unitaires** : règles d'autorisation badge (actif/expiré/horaire).
- **Sécurité** : verrouillage après 3 échecs de connexion.
- **Intégration** : flux complet `scan badge → décision → insertion log`.
- **Critère de démo** : 100% des scénarios « autorisé/refusé » passent sur données de test.

## 12. Plan de livraison (itérations courtes)

- **Itération 0 — Squelette** : projet Spring Boot + Gradle Wrapper, dépendances,
  configuration PostgreSQL + données de démo (seed).
- **Itération 1 — Authentification** : login/logout, BCrypt, rôles, verrouillage 3 échecs,
  journal des connexions.
- **Itération 2 — Contrôle d'accès** : écran agent, saisie/scan badge simulé, règles,
  affichage `AUTORISÉ`/`REFUSÉ` + motif.
- **Itération 3 — Journal immuable** : persistance des événements, écran de consultation
  (recherche par badge/date/décision).
- **Itération 4 — Finitions démo** : données de test réalistes, polissage UI minimal,
  scénarios de démonstration.

## 13. Périmètre de la soutenance (démo)

1. Connexion admin → connexion agent.
2. Scan badge valide → `AUTORISÉ`.
3. Scan badge expiré / hors horaire → `REFUSÉ` + motif.
4. Consultation du journal montrant toutes les tentatives.
5. Démonstration du verrouillage après 3 échecs de connexion.
