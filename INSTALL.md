# SGVAC — Guide d'installation

Application de gestion et de traçabilité des accès sur chantier.

## Configuration requise

- **Windows 10 ou 11**, 64 bits
- ~400 Mo d'espace disque
- **Rien d'autre à installer** : Java et la base de données sont inclus dans l'application.

## Installation

1. Récupérez le fichier **`SGVAC Setup 1.0.0.exe`**.
2. Double-cliquez dessus.
3. **Avertissement Windows (SmartScreen)** : l'application n'étant pas signée par un
   éditeur commercial, Windows peut afficher « Windows a protégé votre ordinateur ».
   Cliquez sur **« Informations complémentaires »** puis **« Exécuter quand même »**.
4. Choisissez le dossier d'installation (ou laissez celui par défaut), puis **Installer**.
5. À la fin, lancez **SGVAC** (raccourci créé sur le Bureau et dans le menu Démarrer).

## Premier lancement

- Au démarrage, l'application lance son moteur interne : **patientez 15 à 30 secondes**
  la première fois, le temps que la fenêtre s'ouvre.
- Si Windows affiche une demande de **pare-feu**, autorisez l'accès sur les **réseaux
  privés** (l'application ne communique qu'en local, sur votre machine).

## Connexion

Comptes de démonstration :

| Identifiant | Mot de passe | Rôle  |
|-------------|--------------|-------|
| `admin`     | `admin123!`  | ADMIN |
| `agent`     | `agent123!`  | AGENT |

## Utilisation rapide

1. **Tableau de bord** — vue d'ensemble des tentatives d'accès.
2. **Poste de contrôle** — saisissez un code badge pour obtenir la décision (autorisé / refusé) :
   | Badge   | Résultat attendu |
   |---------|------------------|
   | `B-001` | ✔ Autorisé |
   | `B-002` | ✘ Refusé — badge expiré |
   | `B-003` | ✔/✘ selon l'heure (autorisé 08h–11h) |
   | `B-004` | ✘ Refusé — badge inactif |
   | autre   | ✘ Refusé — badge inconnu |
3. **Journal** — historique complet et filtrable de toutes les tentatives.

## Où sont stockées les données ?

Dans votre dossier personnel : `C:\Users\<votre-nom>\.sgvac\`
(base de données locale). Les données persistent entre les lancements.

## Désinstallation

Paramètres Windows → **Applications** → **SGVAC** → **Désinstaller**
(ou via l'entrée « Désinstaller SGVAC » du menu Démarrer).

## En cas de problème

- **La fenêtre reste noire / vide** : patientez quelques secondes de plus au premier
  lancement (le moteur interne démarre). Fermez puis rouvrez si besoin.
- **« Le port est déjà utilisé »** : une autre instance de SGVAC tourne déjà —
  fermez-la, ou redémarrez l'ordinateur.
- **SmartScreen bloque l'installation** : voir l'étape 3 ci-dessus.
