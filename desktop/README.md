# SGVAC — Application de bureau (Electron)

Empaquette l'UI **Next.js** (export statique) et le backend **Spring Boot + H2** dans
un seul logiciel Windows. Au lancement, l'app démarre le backend en tâche de fond,
sert l'UI sur un port local et l'affiche dans une fenêtre native. Tout s'arrête à la
fermeture.

## Architecture

| Élément | Détail |
|---|---|
| Fenêtre | Electron (`main.js`) |
| UI | `frontend/out` (export statique Next.js), servie sur `http://localhost:8090` |
| Backend | `sgvac.jar` lancé avec le profil `desktop` (H2 dans `~/.sgvac`, port 8080) |
| JRE | embarqué (`jlink`), donc Java n'a pas besoin d'être installé |

## Pré-requis pour builder

- Node.js (déjà utilisé pour le front)
- Le JDK 21 du projet (pour `jlink` et le build du jar)

## Construire l'installeur

Depuis la racine du dépôt :

```bash
# 1. Construire l'export statique du front
cd frontend && npm run build && cd ..

# 2. Construire le jar backend
./gradlew bootJar          # produit build/libs/sgvac-0.0.1-SNAPSHOT.jar

# 3. Préparer les ressources embarquées (régénérables, non versionnées)
cd desktop
mkdir -p resources/backend resources/jdkpatch
cp ../build/libs/sgvac-0.0.1-SNAPSHOT.jar resources/backend/sgvac.jar
jlink --add-modules ALL-MODULE-PATH --strip-debug --no-man-pages --no-header-files --output resources/jre
#   (sur cette machine uniquement) copier le patch JDK AF_UNIX :
#   cp -r F:/GradleHome/jdkpatch/classes resources/jdkpatch/

# 4. Installer les dépendances et produire l'installeur
npm install
npm run dist               # → dist/SGVAC Setup <version>.exe
```

## Lancer en mode dev (sans empaqueter)

```bash
cd frontend && npm run build && cd ..   # produit frontend/out
./gradlew bootJar
cd desktop && npm install && npm start
```

> **Note machine de dev** : ce poste bloque AF_UNIX, donc le backend a besoin du patch
> `--patch-module`. En dev, exporter `SGVAC_JDK_PATCH` vers le dossier des classes du
> patch avant `npm start`. En empaqueté, le patch est embarqué dans `resources/jdkpatch`.
> Sur un poste Windows standard, le patch est inutile (et ignoré s'il est absent).

## Ce qui est versionné

Seules les **sources** (`main.js`, `package.json`, `package-lock.json`) sont commitées.
`node_modules/`, `dist/` et `resources/` (jar, JRE, patch) sont régénérés par le build.
