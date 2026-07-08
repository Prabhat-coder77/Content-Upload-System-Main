# CCU-SIT — Course Content Upload Platform

A full-stack course content management platform. Users can upload, manage, and download course materials (PDF, MP4, JPG, PNG). Built with a **Spring Boot** backend, **React + Vite** frontend, and **PostgreSQL** database — fully containerized with Docker and automatically deployed to AWS EC2 via GitHub Actions.

---

## [YouTube Link](https://youtu.be/cdT9LzLlQ-k)

# [Live Demo](http://3.94.111.96)

# [http://3.94.111.96](http://3.94.111.96)

# Architecture Overview

```
GitHub (push to main)
        │
        ▼
GitHub Actions (CI/CD)
        │
        ├── rsync → EC2 Instance
        │
        └── SSH → docker compose up --build
                        │
              ┌─────────┼─────────┐
              │         │         │
           frontend   backend    db
        (Nginx:80) (Spring:8080) (Postgres:5432)
```

---

## Project Structure

```
CCU-SIT/
├── ccu-backend/           # Spring Boot API
│   ├── Dockerfile
│   └── src/...
├── ccu-frontend/          # React + Vite SPA
│   ├── Dockerfile
│   ├── nginx.conf
│   └── src/...
├── docker-compose.yml     # Root orchestration file
├── .env.example           # Template for .env secrets
└── .github/
    └── workflows/
        └── deploy.yml     # GitHub Actions CI/CD
```

---

## Run with Docker

```bash
# 1. Copy the env template and fill in your values
cp .env.example .env

# 2. Build and start all services (db + backend + frontend)
docker compose up -d --build

# 3. Open the app
# Frontend → http://localhost
# Backend  → http://localhost:8080/actuator/health
```

```bash
# View logs
docker compose logs -f

# Stop everything
docker compose down
```

---

## Run Locally (Without Docker)

> **Prerequisites:** Java 17, Node.js 20+, PostgreSQL running locally

### Backend

```bash
cd ccu-backend
cp .env.example .env       # Edit DB_*, JWT_SECRET etc.
./mvnw spring-boot:run
# API available at http://localhost:8080
```

### Frontend

```bash
cd ccu-frontend
cp .env.example .env       # Set VITE_API_BASE_URL=http://localhost:8080
npm install
npm run dev
# App available at http://localhost:5173
```
