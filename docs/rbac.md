# Contrôle d'accès (RBAC)

## Rôles

**Rôle global** (porté par le JWT, claim `role`) :

| Rôle | Description |
|---|---|
| `USER` | Attribué à l'inscription |
| `ADMIN` | Accès à `/api/v1/admin/**` ; traité comme `OWNER` sur **tous** les projets |

**Rôle de projet** (table `project_members`) :

| Rôle | Description |
|---|---|
| `OWNER` | Créateur du projet ; unique, non retirable |
| `MEMBER` | Contribue aux tâches |
| `VIEWER` | Lecture seule |

## Matrice des permissions

| Action | VIEWER | MEMBER | OWNER | ADMIN |
|---|:-:|:-:|:-:|:-:|
| Lire le projet, ses membres et ses tâches | ✅ | ✅ | ✅ | ✅ |
| Créer / modifier / changer le statut / supprimer une tâche | ❌ | ✅ | ✅ | ✅ |
| Modifier ou supprimer le projet | ❌ | ❌ | ✅ | ✅ |
| Ajouter / retirer un membre | ❌ | ❌ | ✅ | ✅ |
| Lister tous les projets | — | — | — | ✅ |
| Lister tous les utilisateurs (`/admin/users`) | ❌ | ❌ | ❌ | ✅ |

Tout utilisateur authentifié peut créer un projet (il en devient `OWNER`).

## Règles

- **Non-membre → `404`.** Un projet dont on n'est pas membre est indistinguable d'un projet inexistant.
- **Membre sans droit suffisant → `403`.**
- **Un assigné doit être membre du projet** (sinon `409`).
- **L'`OWNER` ne peut ni être retiré ni dupliqué** ; on ne peut pas ajouter un second `OWNER` (`409`).
- Les droits sont évalués côté serveur à chaque requête, à partir de l'appartenance en base. Le rôle global vient
  du JWT : un changement de rôle global n'est donc effectif qu'à l'expiration du token (voir
  [ADR 0002](adr/0002-jwt-stateless-auth.md)).

## Où c'est implémenté

| Niveau | Emplacement |
|---|---|
| Authentification, `/admin/**` | `infrastructure/security/SecurityConfig` |
| Défense en profondeur sur l'admin | `@PreAuthorize` dans `web/admin/AdminController` |
| Droits par projet | `application/common/ProjectAccess` + `domain/model/ProjectRole` |
| Tests | `TaskServiceRbacTest` (unitaire), `ApiIntegrationTest` (bout en bout) |
