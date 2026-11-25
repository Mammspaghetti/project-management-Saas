# ADR 0001 — Clean Architecture dans un module unique

- **Statut** : accepté
- **Date** : 2026-11-25

## Contexte

Le projet doit démontrer une architecture maintenable. Il faut isoler la logique métier (projets, rôles, tâches) du
framework, de la base de données et de HTTP, sans complexité excessive pour un projet de cette taille.

## Décision

Un **seul module Maven** découpé en quatre packages (`domain`, `application`, `infrastructure`, `web`) avec la règle
de dépendance de la Clean Architecture. La règle est **vérifiée par ArchUnit** dans la suite de tests.

## Alternatives écartées

- **Multi-modules Maven** (un module par couche) : frontières garanties par le compilateur, mais plus de
  configuration et de friction pour peu de bénéfice ici. ArchUnit donne la même garantie au build.
- **Architecture en couches classique** (controller → service → repository JPA) : plus simple, mais le métier devient
  dépendant de JPA et de Spring, et se teste plus difficilement.

## Conséquences

- (+) Le domaine et les cas d'usage se testent sans Spring ni base (`TaskServiceRbacTest`).
- (+) Changer de persistance ou de transport n'impacte pas le métier.
- (−) Du code de conversion (entité JPA ↔ domaine, domaine ↔ DTO).
- (−) `@Transactional` reste présent dans `application` (compromis assumé, explicite dans ArchUnit).
