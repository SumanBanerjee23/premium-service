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

## Technologie-Stack

| Komponente | Version | Beschreibung |
|-----------|---------|-------------|
| Java | 26 | Programmiersprache |
| Spring Boot | 4.0.6 | Anwendungsframework |
| Spring Web MVC | 4.0.6 | REST API Framework |
| Spring Data JPA | 4.0.6 | Datenbank ORM |
| H2 Database | Runtime | In-Memory-Datenbank |
| SpringDoc OpenAPI | 2.7.0 | API-Dokumentation |
| Jakarta Validation | 4.0.6 | Eingabevalidierung |
| Maven | 3.13.0 | Build-Tool |
| Mockito | 5.14.2 | Testing-Framework |
| AssertJ | 3.26.3 | Assertion-Bibliothek |

## Systemanforderungen

- **Java**: JDK 26 oder höher
- **Maven**: 3.6.0 oder höher
- **Betriebssystem**: Windows 10+, macOS 10.15+ oder Linux (Ubuntu 18.04+)
- **Arbeitsspeicher**: Minimum 512MB RAM, Empfohlen 1GB+
- **Festplattenspeicher**: 100MB für Anwendung und Abhängigkeiten

## Installation und Einrichtung

### Voraussetzungen

Stellen Sie sicher, dass Java 26 und Maven installiert und korrekt konfiguriert sind:

```bash
# Java-Installation überprüfen
java -version

# Maven-Installation überprüfen
mvn -version
```

### Anwendung erstellen

```bash
# Repository klonen (falls zutreffend)
git clone <repository-url>
cd premium-service

# Anwendung erstellen
mvn clean install

# Tests während des Erstellens überspringen (optional)
mvn clean install -DskipTests
```

### Anwendung ausführen

#### Option 1: Als Spring Boot Anwendung (Entwicklung)

```bash
# Mit Maven ausführen
mvn spring-boot:run

# Mit spezifischem Profil ausführen
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

#### Option 2: Als ausführbare JAR (Produktion)

```bash
# Die verpackte JAR ausführen
java -jar target/premium-service-0.0.1-SNAPSHOT.jar

# Mit spezifischem Profil ausführen
java -jar target/premium-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod

# Mit benutzerdefiniertem Port ausführen
java -jar target/premium-service-0.0.1-SNAPSHOT.jar --server.port=8081
```

Die Anwendung startet standardmäßig auf `http://localhost:8080`.

## API-Endpunkte

### Basis-URL
```
http://localhost:8080/api/premium
```

### 1. Versicherungsprämie berechnen

**Endpoint**: `POST /calculate`

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
- `400 Bad Request`: Ungültige Eingabedaten
- `404 Not Found`: Postleitzahl nicht gefunden
- `500 Internal Server Error`: Serverfehler

**Beispielanfrage**:
```bash
curl -X POST http://localhost:8080/api/premium/calculate \
  -H "Content-Type: application/json" \
  -d '{
    "annualMileage": 15000,
    "postalCode": "79189",
    "vehicleType": "CAR"
  }'
```

### 2. Prämienberechnung nach ID abrufen

**Endpoint**: `GET /calculations/{id}`

**Beschreibung**: Ruft eine spezifische Prämienberechnung über ihren eindeutigen Bezeichner ab.

**Pfadparameter**:
- `id` (Long): Berechnungs-ID

**Antwort** (200 OK):
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
- `200 OK`: Berechnung gefunden
- `404 Not Found`: Berechnung nicht gefunden

**Beispielanfrage**:
```bash
curl -X GET http://localhost:8080/api/premium/calculations/1
```

### 3. Verfügbare Fahrzeugtypen abrufen

**Endpoint**: `GET /vehicle-types`

**Beschreibung**: Gibt eine Liste der unterstützten Fahrzeugtypen und ihrer Risikofaktoren zurück.

**Antwort** (200 OK):
```json
{
  "CAR": 1.0,
  "MOTORCYCLE": 1.2,
  "TRUCK": 1.5,
  "VAN": 1.3
}
```

**Antwortcodes**:
- `200 OK`: Fahrzeugtypen erfolgreich abgerufen

**Beispielanfrage**:
```bash
curl -X GET http://localhost:8080/api/premium/vehicle-types
```

### 4. Regionsfaktoren abrufen

**Endpoint**: `GET /region-factors`

**Beschreibung**: Gibt Regionsfaktoren für alle deutschen Bundesländer zurück.

**Antwort** (200 OK):
```json
{
  "Baden-Württemberg": 1.2,
  "Bayern": 1.1,
  "Berlin": 1.3,
  "Brandenburg": 0.9,
  "Bremen": 1.1,
  "Hamburg": 1.2,
  "Hessen": 1.1,
  "Mecklenburg-Vorpommern": 0.8,
  "Niedersachsen": 1.0,
  "Nordrhein-Westfalen": 1.2,
  "Rheinland-Pfalz": 1.1,
  "Saarland": 1.0,
  "Sachsen": 0.9,
  "Sachsen-Anhalt": 0.8,
  "Schleswig-Holstein": 1.1,
  "Thüringen": 0.9
}
```

**Antwortcodes**:
- `200 OK`: Regionsfaktoren erfolgreich abgerufen

**Beispielanfrage**:
```bash
curl -X GET http://localhost:8080/api/premium/region-factors
```

### 5. Durchschnittsprämie nach Fahrzeugtyp

**Endpoint**: `GET /statistics/average-premium/{vehicleType}`

**Beschreibung**: Berechnet die Durchschnittsprämie für einen bestimmten Fahrzeugtyp.

**Pfadparameter**:
- `vehicleType` (String): Fahrzeugtyp (z.B. CAR, MOTORCYCLE)

**Antwort** (200 OK):
```json
{
  "vehicleType": "CAR",
  "averagePremium": 1.85
}
```

**Antwortcodes**:
- `200 OK`: Durchschnittsprämie berechnet
- `404 Not Found`: Keine Berechnungen für Fahrzeugtyp gefunden

**Beispielanfrage**:
```bash
curl -X GET http://localhost:8080/api/premium/statistics/average-premium/CAR
```

## API-Dokumentation (Swagger)

Der Service umfasst umfassende API-Dokumentation, die von SpringDoc OpenAPI bereitgestellt wird.

### Zugriff auf Swagger UI

**URL**: `http://localhost:8080/swagger-ui.html`

Die Swagger UI bietet:
- Interaktive API-Testoberfläche
- Detaillierte Endpunkt-Dokumentation
- Anfrage/Antwort-Schemata
- Beispielanfragen und -antworten

### Zugriff auf OpenAPI-Spezifikation

**URL**: `http://localhost:8080/v3/api-docs`

Dies gibt die OpenAPI 3.0-Spezifikation im JSON-Format zurück, die mit verschiedenen API-Dokumentationstools verwendet werden kann.

## Datenmodelle

### PremiumCalculationRequest

```json
{
  "annualMileage": "integer (erforderlich)",
  "postalCode": "string (erforderlich)",
  "vehicleType": "string (erforderlich, enum: CAR, MOTORCYCLE, TRUCK, VAN)"
}
```

### PremiumCalculationResponse

```json
{
  "id": "long",
  "annualMileage": "integer",
  "postalCode": "string",
  "vehicleType": "string",
  "state": "string",
  "city": "string",
  "mileageFactor": "double",
  "vehicleTypeFactor": "double",
  "regionFactor": "double",
  "calculatedPremium": "double",
  "calculatedAt": "datetime"
}
```

## Fehlerbehandlung

Die API implementiert Standard-HTTP-Statuscodes für Fehlerantworten:

- **400 Bad Request**: Validierungsfehler oder fehlerhafte Anfrage
- **404 Not Found**: Ressource nicht gefunden (Postleitzahl, Berechnungs-ID)
- **500 Internal Server Error**: Unerwartete Serverfehler

Fehlerantworten enthalten beschreibende Meldungen zur Unterstützung bei der Fehlerbehebung.

## Protokollierung

Die Anwendung verwendet SLF4J für strukturierte Protokollierung:
- Anfrage/Antwort-Protokollierung für Prämienberechnungen
- Warnungsprotokolle für ungültige Postleitzahlen
- Informationsprotokolle für erfolgreiche Operationen

## Tests

Die Anwendung umfasst umfassende Testabdeckung:
- Unit-Tests für die Service-Schicht
- Integrationstests für Controller
- Testdaten-Fixtures für konsistente Tests

Tests ausführen mit:
```bash
mvn test

# Tests mit Abdeckung ausführen
mvn test jacoco:report
```

## Konfiguration

### Standardkonfiguration
- **Server-Port**: 8080
- **Datenbank**: H2 In-Memory-Datenbank
- **API-Basispfad**: /api/premium

### Umgebungsvariablen
- `SERVER_PORT`: Standard-Server-Port überschreiben
- `SPRING_PROFILES_ACTIVE`: Aktives Profil festlegen (dev, prod)

## Sicherheitsüberlegungen

- Eingabevalidierung auf allen Endpunkten
- SQL-Injection-Prävention durch JPA
- Keine Offenlegung sensibler Daten in Fehlermeldungen
- CORS-Konfiguration für Cross-Origin-Anfragen (falls erforderlich)

## Performance-Überlegungen

- Datenbank-Connection-Pooling über HikariCP
- Effiziente Abfrageoptimierung mit JPA
- Antwort-Caching für statische Daten (Fahrzeugtypen, Regionsfaktoren)

## Unterstützung

Für technischen Support oder Fragen zur API:
1. Überprüfen Sie die Swagger UI für interaktive Dokumentation
2. Überprüfen Sie Anwendungsprotokolle auf Fehlerdetails
3. Konsultieren Sie die Testfälle für Verwendungsbeispiele
