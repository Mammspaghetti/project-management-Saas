# API REST

Base : `/api/v1`. Format : JSON. La référence interactive (OpenAPI) est servie par le backend :

- Swagger UI : `http://localhost:8080/swagger-ui.html`
- Spécification : `http://localhost:8080/v3/api-docs`

## Authentification

Les endpoints (sauf `register` et `login`) exigent l'en-tête `Authorization: Bearer <accessToken>`.

```http
POST /api/v1/auth/login
Content-Type: application/json

{ "email": "jane@example.com", "password": "Passw0rd!" }
```

```json
{
  "accessToken": "eyJhbGciOi...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "user": { "id": 1, "email": "jane@example.com", "displayName": "Jane", "role": "USER" }
}
```

## Endpoints

Accès : « Auth » = utilisateur authentifié ; les droits par projet sont détaillés dans [rbac.md](rbac.md).

### Auth

| Méthode | Chemin | Accès | Description | Succès |
|---|---|---|---|---|
| POST | `/auth/register` | Public | Crée un compte et retourne un token | 201 |
| POST | `/auth/login` | Public | Authentifie et retourne un token | 200 |
| GET | `/auth/me` | Auth | Utilisateur courant | 200 |

Règles : mot de passe 8 à 72 caractères ; email unique (409 sinon) ; identifiants invalides → 401 (même message
pour email inconnu et mauvais mot de passe).

### Projets

| Méthode | Chemin | Accès | Description | Succès |
|---|---|---|---|---|
| GET | `/projects` | Auth | Projets dont je suis membre (tous pour un ADMIN) | 200 |
| POST | `/projects` | Auth | Crée un projet (je deviens OWNER) | 201 |
| GET | `/projects/{projectId}` | Membre | Détail | 200 |
| PUT | `/projects/{projectId}` | OWNER | Modifie nom / description | 200 |
| DELETE | `/projects/{projectId}` | OWNER | Supprime le projet et ses tâches | 204 |

### Membres

| Méthode | Chemin | Accès | Description | Succès |
|---|---|---|---|---|
| GET | `/projects/{projectId}/members` | Membre | Liste des membres et rôles | 200 |
| POST | `/projects/{projectId}/members` | OWNER | Ajoute un utilisateur par email, rôle `MEMBER` ou `VIEWER` | 201 |
| DELETE | `/projects/{projectId}/members/{userId}` | OWNER | Retire un membre | 204 |

### Tâches

| Méthode | Chemin | Accès | Description | Succès |
|---|---|---|---|---|
| GET | `/projects/{projectId}/tasks?status=TODO` | Membre | Liste, filtre `status` optionnel | 200 |
| POST | `/projects/{projectId}/tasks` | MEMBER+ | Crée une tâche (statut initial `TODO`) | 201 |
| GET | `/projects/{projectId}/tasks/{taskId}` | Membre | Détail | 200 |
| PUT | `/projects/{projectId}/tasks/{taskId}` | MEMBER+ | Modifie la tâche | 200 |
| PATCH | `/projects/{projectId}/tasks/{taskId}/status` | MEMBER+ | Change le statut `{ "status": "DONE" }` | 200 |
| DELETE | `/projects/{projectId}/tasks/{taskId}` | MEMBER+ | Supprime | 204 |

Corps de création/modification d'une tâche :

```json
{
  "title": "Maquette de la page d'accueil",
  "description": "optionnel",
  "priority": "HIGH",
  "assigneeId": 2,
  "dueDate": "2026-12-15"
}
```

Valeurs : `status` ∈ `TODO | IN_PROGRESS | DONE` ; `priority` ∈ `LOW | MEDIUM | HIGH` (défaut `MEDIUM`).

### Administration

| Méthode | Chemin | Accès | Description |
|---|---|---|---|
| GET | `/admin/users` | ADMIN | Liste des utilisateurs |

### Santé

`GET /actuator/health` (public) — sondes `liveness` / `readiness` sous `/actuator/health/*`.

## Erreurs

Toutes les erreurs suivent la [RFC 7807](https://datatracker.ietf.org/doc/html/rfc7807) (`application/problem+json`).

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Validation failed",
  "errors": { "email": "must be a well-formed email address" }
}
```

| Statut | Signification |
|---|---|
| 400 | Corps invalide (`errors` détaille les champs) |
| 401 | Token absent / invalide / expiré, ou identifiants incorrects |
| 403 | Authentifié mais rôle insuffisant |
| 404 | Ressource inexistante **ou non accessible** (non-membre) |
| 409 | Conflit métier (email déjà pris, assigné non membre, retrait de l'owner…) |
