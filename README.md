# LiveScore 🏆📱

LiveScore is a full-stack sports tournament management and live score tracking application designed to manage tournaments, teams, players, fixtures, and real-time match updates.

It provides separate functionalities for Admin, Team Leaders, Players, and Users.

---

## 🚀 Features

### 👨‍💼 Admin Features
- Create tournaments
- Manage teams
- Add players
- Schedule matches
- Update live scores
- Manage points table
- View player statistics

### 👥 Team Leader Features
- Register team
- Add players
- View tournament fixtures
- Check team standings

### 🧑 Player Features
- View match schedule
- Check personal stats
- Participate in tournaments

### 👤 User Features
- View live matches
- Check points table
- View tournaments
- Follow teams and players

---

## 🏗️ Project Architecture

This project follows full-stack architecture:

LiveScore/
│
├── frontend/ (Flutter App)
├── backend/ (Spring Boot API)
├── firebase/ (Firestore Database)

---

## 🛠️ Tech Stack

### Frontend
- Flutter
- Dart

### Backend
- Spring Boot
- Spring Security
- JWT Authentication
- REST API

### Database
- Firebase Firestore

### Deployment
- Docker
- Render / Railway (Backend)

---

## 📂 Backend Modules

- Authentication Module
- Tournament Module
- Team Module
- Player Module
- Match Module
- Points Table Module
- Statistics Module

---

## 🔐 Roles Used

```java
ADMIN,
USER,
TEAM_LEADER,
PLAYER
