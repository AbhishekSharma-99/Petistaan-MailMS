# Petistaan-MailMS

Mail microservice for the [Petistaan-MS](https://github.com/AbhishekSharma-99/Petistaan-MS)
pet management system. Receives lifecycle events from OwnerMS and dispatches transactional
emails to owners using FreeMarker templates resolved from a `MailType` enum.

> **To run the full system** (all services + MySQL + Eureka + Config Server), see the
> [Petistaan-MS](https://github.com/AbhishekSharma-99/Petistaan-MS) hub repo.
> The `docker-compose.yml` lives there.

---

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Mail Types](#mail-types)
- [API Reference](#api-reference)
- [Project Structure](#project-structure)
- [Configuration](#configuration)
- [Running Locally](#running-locally)
- [Branch Strategy](#branch-strategy)
- [Part of Petistaan-MS](#part-of-petistaan-ms)

---

## Overview

MailMS is a standalone Spring Boot microservice responsible for:

- Accepting inbound `MailDTO` payloads from other services in the ecosystem (primarily OwnerMS)
- Resolving the `MailType` enum to the appropriate FreeMarker email template
- Rendering and dispatching transactional emails to owners on every lifecycle event —
  registration (`WELCOME`), pet rename (`MODIFY`), and owner deletion (`EXIT`)

MailMS is a **passive consumer** — it exposes a single `POST /mails` endpoint and is
driven entirely by inter-service calls. It does not initiate any outbound HTTP calls of its own.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 25 |
| Framework | Spring Boot 4.1.0 |
| Templating | FreeMarker |
| Mail | Spring Mail (JavaMailSender) |
| Build | Maven 3.9.x (Maven Wrapper) |

---

## Mail Types

`MailType` is the core enum that drives template resolution. Each value maps to a dedicated
FreeMarker template and a pre-defined subject line.

| MailType | Trigger | Subject | Template |
|---|---|---|---|
| `WELCOME` | Owner + pet registered | `Welcome to Petistaan!` | `welcome.ftlh` |
| `MODIFY` | Pet name updated | `Your data in Petistaan has been modified` | `modify.ftlh` |
| `EXIT` | Owner and pet deleted | `Thanks for visiting Petistaan` | `exit.ftlh` |

---

## API Reference

Base URL: `http://localhost:8083`

| Method | Endpoint | Request Body | Response | Description |
|---|---|---|---|---|
| `POST` | `/mails` | `MailDTO` | `200` | Render and dispatch a lifecycle email |

### Sample request — dispatch a welcome email

```json
POST /mails
{
  "to": "abhisheksharma@example.com",
  "firstName": "Abhishek",
  "lastName": "Sharma",
  "category": "WELCOME"
}
```

> `category` must be one of `WELCOME`, `MODIFY`, or `EXIT`.
> The correct FreeMarker template and subject are resolved internally from the enum.

### Error response shape

```json
{
  "message": "Failed to dispatch email — invalid mail type provided.",
  "httpStatus": "BAD_REQUEST",
  "value": 400,
  "now": "2025-09-01T14:32:00"
}
```

---

## Project Structure

```
src/main/java/com/abhishek/
├── config/
│   └── MailConfig.java                # JavaMailSender bean configuration
├── controller/
│   ├── advice/
│   │   └── ExceptionControllerHandler.java   # Global exception handler
│   └── MailController.java
├── dto/
│   ├── MailDTO.java                   # Inbound payload from OwnerMS
│   └── ErrorDTO.java                  # Uniform error body
├── enums/
│   └── MailType.java                  # WELCOME, MODIFY, EXIT — with subject + template name
├── service/
│   ├── impl/
│   │   └── MailServiceImpl.java       # Resolves MailType → template, renders, dispatches
│   └── MailService.java
└── PetistaanMailMsApplication.java

src/main/resources/
├── templates/
│   ├── welcome.ftlh                   # FreeMarker — new owner registration email
│   ├── modify.ftlh                    # FreeMarker — pet rename notification email
│   └── exit.ftlh                      # FreeMarker — owner deletion farewell email
└── application.properties
```

---

### Configuration

All sensitive or environment-specific values are externalized. Copy `.env.example` to `.env` and fill in your values:

```dotenv
SYSTEM_EMAIL_ID=mailpit@petistaan.local
SYSTEM_EMAIL_PASSWORD=any_dummy_password

```

> **Local Testing with Mailpit:** Mailpit's default SMTP server runs on `localhost:1025` and does not require strict credential authentication. However, because the application properties bind to these environment variables, you must provide dummy strings (as shown above) in your `.env` file so the Spring context initializes without failing.

`application.properties` resolves these values at runtime. The `.env` file is gitignored and never committed.

Key properties:

| Property | Value |
| --- | --- |
| `spring.application.name` | `Petistaan-MailMS` |
| `server.port` | `8083` |
| `spring.mail.host` | `localhost` |
| `spring.mail.port` | `1025` |
| `spring.mail.properties.mail.smtp.auth` | `false` |
| `spring.mail.properties.mail.smtp.starttls.enable` | `false` |
| `spring.mail.username` | `${SYSTEM_EMAIL_ID}` |
| `spring.mail.password` | `${SYSTEM_EMAIL_PASSWORD}` |

---

### Running Locally

**Prerequisites:** Java 25, a running Mailpit instance (SMTP port `1025`, Web UI port `8025`).

> To spin up the full environment in one command (including Mailpit), use the `docker-compose.yml` in the [Petistaan-MS](https://github.com/AbhishekSharma-99/Petistaan-MS) hub repo.

To run this service in isolation:

```bash
# 1. Clone the repo
git clone https://github.com/AbhishekSharma-99/Petistaan-MailMS.git
cd Petistaan-MailMS

# 2. Set up environment
cp .env.example .env
# Edit .env with dummy/local values for SYSTEM_EMAIL_ID and SYSTEM_EMAIL_PASSWORD

# 3. Run
./mvnw spring-boot:run

```

* Service starts on `http://localhost:8083`.
* Open your browser and navigate to **`http://localhost:8025`** to view the Mailpit Web UI and inspect the intercepted FreeMarker HTML emails.

---

## Running Locally

**Prerequisites:** Java 25, a valid SMTP account (e.g. Gmail with App Password).

> To spin up the full environment in one command, use the `docker-compose.yml` in the
> [Petistaan-MS](https://github.com/AbhishekSharma-99/Petistaan-MS) hub repo.

To run this service in isolation:

```bash
# 1. Clone the repo
git clone https://github.com/AbhishekSharma-99/Petistaan-MailMS.git
cd Petistaan-MailMS

# 2. Set up environment
cp .env.example .env
# Edit .env with your SMTP credentials

# 3. Run
./mvnw spring-boot:run
```

Service starts on `http://localhost:8083`.

---

## Branch Strategy

```
main  ●──────────────────────────────────────────● (merge via PR)
       \                                         /
dev     ●──●──●──●──●──●──●──●──●──●──●──●─────
```

- `main` — stable, production-ready commits only; no direct pushes
- `dev` — integration branch; all feature work merges here first
- `feature/*` — short-lived branches off `dev` for individual concerns

---

## Part of Petistaan-MS

Petistaan-MailMS is one service in the broader Petistaan microservices ecosystem:

| Service | Port | Responsibility |
|---|---|---|
| [Petistaan-EurekaServer](https://github.com/AbhishekSharma-99/Petistaan-EurekaServer) | 8761 | Service discovery |
| [Petistaan-ConfigServer](https://github.com/AbhishekSharma-99/Petistaan-ConfigServer) | 8888 | Centralized config |
| [Petistaan-APIGateway](https://github.com/AbhishekSharma-99/Petistaan-APIGateway) | 8080 | Single entry point |
| [Petistaan-OwnerMS](https://github.com/AbhishekSharma-99/Petistaan-OwnerMS) | 8081 | Owner + pet management |
| [Petistaan-PetMS](https://github.com/AbhishekSharma-99/Petistaan-PetMS) | 8082 | Pet statistics |
| **Petistaan-MailMS** | **8083** | **Email dispatch** |

See [Petistaan-MS](https://github.com/AbhishekSharma-99/Petistaan-MS) for system
architecture, build order, and Docker Compose setup.