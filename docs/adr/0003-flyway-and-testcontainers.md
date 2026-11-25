# ADR 0003 — Flyway pour le schéma, Testcontainers pour les tests d'intégration

- **Statut** : accepté
- **Date** : 2026-11-25

## Contexte

Le schéma doit être versionné et reproductible, et les tests d'intégration doivent refléter le comportement réel de
MySQL (types, contraintes, `ON DELETE CASCADE`).

## Décision

- **Flyway** applique les migrations SQL au démarrage ; Hibernate est en `ddl-auto: validate`.
- Les tests d'intégration utilisent **Testcontainers** (`mysql:8.4`, `@ServiceConnection`) plutôt qu'une base
  embarquée (H2) : même moteur qu'en production, mêmes migrations.

## Conséquences

- (+) Un schéma, une seule source de vérité ; un écart entre entités et migrations fait échouer le démarrage.
- (+) Pas de divergence de dialecte entre tests et production.
- (−) Docker requis pour lancer les tests d'intégration (le test est ignoré s'il est absent ; la CI l'a toujours).
