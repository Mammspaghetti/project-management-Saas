# ADR 0002 — Authentification JWT sans état

- **Statut** : accepté
- **Date** : 2026-11-25

## Contexte

Le frontend est une SPA qui consomme une API REST. Il faut une authentification simple à scaler, avec un rôle
global exploitable par Spring Security.

## Décision

- Jeton d'accès **JWT signé HS256**, émis par `JwtTokenIssuer`, validé par le support *resource server* de Spring
  Security (Nimbus). Aucune dépendance JWT supplémentaire.
- Claims : `sub` (id utilisateur), `role` (rôle global), `email`, `exp` (60 min par défaut).
- Mots de passe hachés avec **BCrypt**.
- Secret fourni par `JWT_SECRET` (≥ 32 caractères, vérifié au démarrage).
- CSRF désactivé : API sans état, jeton dans l'en-tête `Authorization` (pas de cookie).
- Côté client, le jeton est conservé dans `localStorage`.

## Conséquences

- (+) Backend sans session, simple à répliquer.
- (−) **Pas de révocation** avant expiration ; un changement de rôle global n'est effectif qu'au prochain token.
  Les droits par projet, eux, sont relus en base à chaque requête.
- (−) `localStorage` expose le jeton en cas de faille XSS. Atténuation : durée de vie courte, pas de HTML injecté
  (React échappe par défaut). Évolution prévue : refresh token en cookie `HttpOnly` + access token en mémoire.
- (−) HS256 impose un secret partagé ; une migration vers RS256/OIDC serait la voie si d'autres services
  devaient valider les jetons.
