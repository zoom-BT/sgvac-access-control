# Déploiement SGVAC

L'application est **full-stack** : elle se déploie en deux endroits.

| Partie | Hébergeur | Dossier |
|---|---|---|
| Backend Spring Boot + PostgreSQL | **Render** | racine du dépôt (`Dockerfile`) |
| Frontend Next.js | **Vercel** | `frontend/` |

L'ordre compte : on déploie d'abord le **backend** (pour obtenir son URL), puis le **frontend** (qui pointe vers cette URL), puis on revient régler le **CORS** du backend sur l'URL Vercel.

---

## 1. Backend sur Render

1. Crée un compte sur [render.com](https://render.com) et connecte ton GitHub.
2. **New → Blueprint**, sélectionne le dépôt `sgvac-access-control`.
   Render lit [`render.yaml`](render.yaml) et crée automatiquement :
   - une base **PostgreSQL** gratuite (`sgvac-db`) ;
   - un service web **Docker** (`sgvac-api`) avec les variables de base de données déjà branchées et un `SGVAC_JWT_SECRET` généré.
3. Lance le déploiement. Le premier build (Gradle dans Docker) prend quelques minutes.
4. Note l'URL publique du service, ex. `https://sgvac-api.onrender.com`.
5. Vérifie que l'API répond :
   ```bash
   curl -X POST https://sgvac-api.onrender.com/api/v1/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username":"admin","password":"admin123!"}'
   ```
   Tu dois recevoir un JSON `{ "token": "...", "username": "admin", "role": "ADMIN" }`.

> **Note** : sur le plan gratuit, le service s'endort après inactivité (premier appel ~50 s) et la base Postgres gratuite expire après 90 jours. Suffisant pour une démo / soutenance.

Les comptes de démonstration (`admin`/`admin123!`, `agent`/`agent123!`) et les badges sont créés automatiquement au premier démarrage par le `DataSeeder`.

---

## 2. Frontend sur Vercel

1. Crée un compte sur [vercel.com](https://vercel.com) et connecte ton GitHub.
2. **Add New → Project**, sélectionne le dépôt.
3. **Réglage important** — *Root Directory* : `frontend`
   (Vercel détecte alors Next.js automatiquement.)
4. Dans **Environment Variables**, ajoute :
   | Nom | Valeur |
   |---|---|
   | `NEXT_PUBLIC_API_URL` | l'URL Render du backend, ex. `https://sgvac-api.onrender.com` |
5. **Deploy**. Note l'URL Vercel, ex. `https://sgvac.vercel.app`.

---

## 3. Autoriser le frontend côté backend (CORS)

Le backend n'accepte par défaut que `http://localhost:3000`. Pour autoriser le domaine Vercel :

1. Render → service `sgvac-api` → **Environment** → ajoute/édite :
   | Nom | Valeur |
   |---|---|
   | `SGVAC_CORS_ALLOWED_ORIGINS` | `https://sgvac.vercel.app` |
   (plusieurs domaines possibles, séparés par des virgules)
2. Render redéploie automatiquement.

C'est tout : ouvre l'URL Vercel et connecte-toi.

---

## Variables d'environnement du backend (référence)

| Variable | Rôle | Géré par |
|---|---|---|
| `SPRING_PROFILES_ACTIVE=prod` | active le profil PostgreSQL | `render.yaml` / `Dockerfile` |
| `DB_HOST` `DB_PORT` `DB_NAME` `DB_USER` `DB_PASSWORD` | connexion Postgres | `render.yaml` (depuis `sgvac-db`) |
| `SGVAC_JWT_SECRET` | clé de signature JWT (≥ 32 caractères) | généré par Render |
| `SGVAC_JWT_EXPIRATION_MS` | durée de validité du token (défaut 24 h) | optionnel |
| `SGVAC_CORS_ALLOWED_ORIGINS` | domaine(s) frontend autorisé(s) | à régler (étape 3) |
| `PORT` | port d'écoute | injecté par Render |

## Développement local (rappel)

- Backend : `./gradlew bootRun` (profil H2 par défaut, http://localhost:8080)
- Frontend : `cd frontend && npm run dev` (http://localhost:3000)
