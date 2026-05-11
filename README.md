# Premium Service API-Dokumentation

## Übersicht

Der Premium Service ist eine Spring Boot Anwendung, die zur Berechnung von Versicherungsprämien basierend auf verschiedenen Faktoren wie Fahrzeugtyp, jährlicher Fahrleistung und geografischem Standort entwickelt wurde. Der Service bietet RESTful APIs für Prämienberechnungen, Datenabruf und statistische Analysen.

## Funktionen

- **Prämienberechnung**: Berechnung von Versicherungsprämien basierend auf Fahrleistung, Fahrzeugtyp und Postleitzahl
- **Regionale Datenverwaltung**: Unterstützung für deutsche Postleitzahlen mit Bundesland- und Stadinformationen
- **Fahrzeugtyp-Unterstützung**: Mehrere Fahrzeugkategorien mit individuellen Risikofaktoren
- **Statistische Analyse**: Durchschnittsprämienberechnungen nach Fahrzeugtyp
- **Datenpersistenz**: H2-Datenbank zur Speicherung der Berechnungshistorie
- **API-Dokumentation**: Integrierte Swagger/OpenAPI-Dokumentation
- **Eingabevalidierung**: Umfassende Anfragevalidierung mit Jakarta Validation

## Architektur-Hinweise

Die Anwendung trennt Eingabedaten (`CustomerRequest`) von Berechnungsergebnissen (`PremiumCalculation`) als gute architektonische Praxis. Diese Trennung ermöglicht Audit-Trails, unterstützt zukünftige Anforderungen für mehrere Berechnungen pro Anfrage und erhält klare Domänengrenzen.

## Technologie-Stack

| Komponente | Version | Beschreibung |
|-----------|---------|-------------|
| Java | 21 | Programmiersprache |
| Spring Boot | 4.0.6 | Anwendungsframework |
| Spring Web MVC | 4.0.6 | REST API Framework |
| Spring Data JPA | 4.0.6 | Datenbank ORM |
| H2 Database | Runtime | Dateibasierte Datenbank |
| SpringDoc OpenAPI | 2.7.0 | API-Dokumentation |
| Jakarta Validation | 4.0.6 | Eingabevalidierung |
| Maven | 3.13.0 | Build-Tool |
| Mockito | 5.14.2 | Testing-Framework |
| AssertJ | 3.26.3 | Assertion-Bibliothek |

## Datenbank-Konfiguration

**Entwicklung**: Verwendet H2 im dateibasierten Modus für die Datenspeicherung über Anwendung-Neustarts hinweg. Datenbankdateien werden im `./data/` Verzeichnis gespeichert.

**Produktion**: Für Produktionsumgebungen wird die Verwendung von PostgreSQL oder anderen Enterprise-Datenbanken für bessere Performance und Zuverlässigkeit empfohlen.

## Systemanforderungen

- **Java**: JDK 21 oder höher
- **Maven**: 3.6.0 oder höher
- **Betriebssystem**: Windows 10+, macOS 10.15+ oder Linux (Ubuntu 18.04+)
- **Arbeitsspeicher**: Minimum 512MB RAM, Empfohlen 1GB+
- **Festplattenspeicher**: 100MB für Anwendung und Abhängigkeiten

## Einrichtung und Start

### Voraussetzungen

Stellen Sie sicher, dass Java 21 und Maven installiert und korrekt konfiguriert sind:

```bash
# Java-Installation überprüfen
java -version

# Maven-Installation überprüfen
mvn -version
```

### Anwendung starten

```bash
# Repository klonen (falls zutreffend)
git clone <repository-url>
cd premium-service

# Anwendung erstellen
mvn clean install

# Anwendung ausführen
mvn spring-boot:run
```

Die Anwendung startet standardmäßig auf `http://localhost:8080`.

### Zugriffspunkte und URLs

#### Web-Oberfläche
- **Startseite**: `http://localhost:8080`
- **Index HTML**: `http://localhost:8080/index.html`

#### API-Endpunkte
- **API Basis-URL**: `http://localhost:8080/api/premium`
- **Prämie berechnen**: `POST /api/premium/calculate`
- **Berechnung abrufen**: `GET /api/premium/calculations/{id}`
- **Fahrzeugtypen**: `GET /api/premium/vehicle-types`
- **Regionsfaktoren**: `GET /api/premium/region-factors`
- **Statistiken**: `GET /api/premium/statistics/average-premium/{vehicleType}`

#### API-Dokumentation
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI Spezifikation**: `http://localhost:8080/v3/api-docs`

#### Datenbank-Zugriff
- **H2 Console**: `http://localhost:8080/h2-console`
    - **JDBC URL**: `jdbc:h2:file:./data/premiumdb`
    - **Benutzername**: `sa`
    - **Passwort**: `password`

#### Management-Endpunkte
- **Health Check**: `http://localhost:8080/actuator/health`
- **Application Info**: `http://localhost:8080/actuator/info`
- **Metrics**: `http://localhost:8080/actuator/metrics`

## API-Endpunkte

### 1. Versicherungsprämie berechnen

**Endpoint**: `POST /api/premium/calculate`

**Beschreibung**: Berechnet die Versicherungsprämie basierend auf Fahrleistung, Fahrzeugtyp und Postleitzahl.

**Anfragekörper**:
```json
{
  "annualMileage": 15000,
  "postalCode": "79189",
  "vehicleType": "CAR"
}
```

**Antwort** (201 Created):
```json
{
  "id": 1,
  "annualMileage": 15000,
  "postalCode": "79189",
  "vehicleType": "CAR",
  "state": "Baden-Württemberg",
  "city": "Bad Krozingen",
  "mileageFactor": 1.5,
  "vehicleTypeFactor": 1.0,
  "regionFactor": 1.2,
  "calculatedPremium": 1.80,
  "calculatedAt": "2024-01-15T10:30:00"
}
```

**Antwortcodes**:
- `201 Created`: Prämie erfolgreich berechnet
- `400 Bad Request`: Ungültige Eingabedaten (unbekannter Fahrzeugtyp)
- `404 Not Found`: Postleitzahl nicht gefunden
- `500 Internal Server Error`: Serverfehler

### 2. Prämienberechnung nach ID abrufen

**Endpoint**: `GET /api/premium/calculations/{id}`

**Beschreibung**: Ruft eine spezifische Prämienberechnung über ihren eindeutigen Bezeichner ab.

**Antwortcodes**:
- `200 OK`: Berechnung gefunden
- `404 Not Found`: Berechnung nicht gefunden

### 3. Verfügbare Fahrzeugtypen abrufen

**Endpoint**: `GET /api/premium/vehicle-types`

**Beschreibung**: Gibt eine Liste der unterstützten Fahrzeugtypen und ihrer Risikofaktoren zurück.

**Antwort** (200 OK):
```json
{
  "CAR": 1.0,
  "MOTORCYCLE": 0.7,
  "TRUCK": 1.5,
  "VAN": 1.2,
  "ELECTRIC_CAR": 0.8,
  "HYBRID_CAR": 0.9,
  "LUXURY_CAR": 1.8,
  "SPORTS_CAR": 1.6,
  "SUV": 1.4
}
```

### 4. Regionsfaktoren abrufen

**Endpoint**: `GET /api/premium/region-factors`

**Beschreibung**: Gibt Regionsfaktoren für alle deutschen Bundesländer zurück.

### 5. Durchschnittsprämie nach Fahrzeugtyp

**Endpoint**: `GET /api/premium/statistics/average-premium/{vehicleType}`

**Beschreibung**: Berechnet die Durchschnittsprämie für einen bestimmten Fahrzeugtyp.

## Fehlerbehandlung

Die API implementiert Standard-HTTP-Statuscodes für Fehlerantworten:

- **400 Bad Request**: Validierungsfehler oder fehlerhafte Anfrage (unbekannter Fahrzeugtyp)
- **404 Not Found**: Ressource nicht gefunden (Postleitzahl, Berechnungs-ID)
- **500 Internal Server Error**: Unerwartete Serverfehler

## Tests

Die Anwendung umfasst umfassende Testabdeckung:
- Unit-Tests für die Service-Schicht
- Integrationstests für Controller
- Testdaten-Fixtures für konsistente Tests
- Grenzwerttests für Kilometergrenzen (5.000, 5.001, 10.000, 10.001, 20.000, 20.001 km)

Tests ausführen mit:
```bash
mvn test

# Tests mit Abdeckung ausführen
mvn test jacoco:report
```

## Konfiguration

### Standardkonfiguration
- **Server-Port**: 8080
- **Datenbank**: H2 Dateibasierte Datenbank
- **API-Basispfad**: /api/premium

### Umgebungsvariablen
- `SERVER_PORT`: Standard-Server-Port überschreiben
- `SPRING_PROFILES_ACTIVE`: Aktives Profil festlegen (dev, prod)

## Produktionsüberlegungen

Für Produktionsumgebungen:
- PostgreSQL oder andere Enterprise-Datenbanken verwenden
- Externe Konfiguration über Umgebungsvariablen
- Monitoring und Logging optimieren
- Security-Aspekte berücksichtigen
