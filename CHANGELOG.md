# Changelog

## [0.1.0] - 2026-11-19

### Προστέθηκε
- Σκελετός Spring Boot 4 με Vaadin 25 και PostgreSQL (Java 25).
- Docker Compose για τη βάση (PostgreSQL 16), με προεπιλογές χωρίς `.env`.
- Αρχείο `.env.example`.
- Αρχιτεκτονική τεκμηρίωση και ADR 001 (Vaadin + REST endpoints).
- Τεκμηρίωση εξωτερικών APIs (Wikidata, Open Library) στο `docs/apis.md`.
- Θέμα και περίγραμμα αποστολής στο `docs/apostoli.md`.
- CI με GitHub Actions (`mvn verify`, Java 25) και στατική ανάλυση PMD.
- README με οδηγίες εγκατάστασης, εκτέλεσης και ελέγχων.
- Pull request template, Definition of Done, αρχείο χρήσης AI και `TEAM.md`.

## [0.2.0] - 2026-11-26

### Προστέθηκε
- Εγγραφή και είσοδος χρηστών με κρυπτογραφημένο κωδικό (BCrypt) και Spring Security.
- Ομάδες παικτών: δημιουργία, είσοδος σε ομάδα, διαχωρισμός δημιουργού και μελών.
- Αποστολή ως δεδομένα: φόρτωση από JSON (`missions/whitby-manuscript.json`).
- Κατάλογος αποστολών, μενού εφαρμογής και έναρξη αποστολής.
- Πρώτος γρίφος με έλεγχο απάντησης στο backend.
- Κοινό ημερολόγιο στοιχείων ανά ομάδα.
- REST endpoint `GET /api/missions` και τεκμηρίωση OpenAPI (`/swagger-ui.html`).
- Docker: εφαρμογή και βάση με `docker compose up`, και workflow που δημοσιεύει Docker image σε κάθε tag έκδοσης.
- Unit tests για όλα τα παραπάνω.

### Άλλαξε
- Αναβάθμιση του `maven-pmd-plugin` σε 3.28.0 για υποστήριξη Java 25.