# SGVAC — Système de Gestion des Voies d'Accès de Chantier

Application de **contrôle et de traçabilité des accès** sur chantier de génie civil :
authentification des opérateurs, vérification de badges, et journal d'accès immuable.

Le projet se décline en trois couches :

| Couche | Techno | Rôle |
|---|---|---|
| **Backend** | Spring Boot 3 (Java 21), Spring Security, JPA, H2 | API REST sécurisée par **JWT** + règles métier |
| **Frontend** | Next.js 16 (App Router), React 19, Tailwind 4 | Interface web (thème chantier) consommant l'API |
| **Desktop** | Electron | Empaquette l'UI + le backend en **logiciel Windows** autonome |

> Pour **installer le logiciel** (utilisateur final), voir [INSTALL.md](INSTALL.md).
> Pour **construire l'installeur**, voir [desktop/README.md](desktop/README.md).

---

## Architecture

```
Navigateur / fenêtre Electron
        │  (JWT Bearer, fetch)
        ▼
  API REST  /api/v1/**         ← chaîne de sécurité stateless (JwtAuthFilter)
        │
  Services métier (AccessControlService, LoginAttemptService, ApiAuthService)
        │
  JPA / H2  (users, badges, access_events)
```

- Authentification : `POST /api/v1/auth/login` → JWT ; `GET /api/v1/auth/me`.
- Contrôle d'accès : `POST /api/v1/access/evaluate` ; journal : `GET /api/v1/access/events`.
- Deux chaînes Spring Security : une **stateless JWT** pour `/api/**`, une **form-login**
  pour l'UI Thymeleaf historique (toujours présente, servie sur `/`).

## Prérequis

- **JDK 21** (le wrapper Gradle est inclus : `gradlew` / `gradlew.bat`)
- **Node.js 20+** (pour le frontend et l'empaquetage desktop)

## Lancer en développement

**1. Backend** (profil H2 par défaut, http://localhost:8080) :
```bash
./gradlew bootRun
```

**2. Frontend** (http://localhost:3000) :
```bash
cd frontend
npm install
npm run dev
```
Le frontend lit l'URL de l'API via `NEXT_PUBLIC_API_URL` (défaut `http://localhost:8080`).

## Construire le logiciel de bureau

Voir [desktop/README.md](desktop/README.md) — en résumé :
```bash
cd frontend && npm run build && cd ..   # export statique → frontend/out
./gradlew bootJar                        # jar backend
cd desktop && npm install && npm run dist  # → desktop/dist/SGVAC Setup <version>.exe
```

## Comptes de démonstration

| Identifiant | Mot de passe | Rôle  |
|-------------|--------------|-------|
| `admin`     | `admin123!`  | ADMIN |
| `agent`     | `agent123!`  | AGENT |

## Badges de démonstration

| Code  | État     | Particularité          | Décision |
|-------|----------|------------------------|----------|
| B-001 | ACTIVE   | valide                 | Autorisé |
| B-002 | EXPIRED  | expiré                 | Refusé |
| B-003 | ACTIVE   | horaires 08:00–11:00   | Selon l'heure |
| B-004 | INACTIVE | désactivé              | Refusé |

## Règles métier

- **RM-01** : badge non actif / expiré / hors plage horaire ⇒ refusé.
- **RM-02** : toute tentative d'accès est journalisée.
- **RM-03** : 3 échecs de connexion ⇒ verrouillage temporaire du compte (15 min).
- **RM-04** : le journal d'accès est immuable (ni modification ni suppression).
- **RM-05** : toute connexion (succès/échec) est journalisée.

## Tests

```bash
./gradlew test
```
Couvre les règles d'autorisation, le verrouillage de connexion, et l'intégration de
l'API JWT.

## Structure du dépôt

```
src/main/java/com/chantier/sgvac/
  access/   règles d'accès + journal immuable (entités, service, repo)
  auth/     JWT (JwtService, JwtAuthFilter, ApiAuthService) + verrouillage
  api/      contrôleurs REST /api/v1 + DTOs
  user/ badge/  domaines
  config/   sécurité, CORS, seeder de démo
  web/      contrôleurs de l'UI Thymeleaf historique
frontend/   application Next.js (UI moderne)
desktop/    application Electron (empaquetage logiciel)
docs/       spécification et plan d'implémentation
```

## Notes

- Profils Spring : `default` (H2 fichier `./data`, dev) · `desktop` (H2 dans `~/.sgvac`,
  utilisé par le logiciel empaqueté). La configuration sensible (secret JWT, origines
  CORS) est externalisée via variables d'environnement.
