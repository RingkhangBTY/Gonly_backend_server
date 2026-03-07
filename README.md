<div align="center">

# 🏔️ GONLY — Hidden Gems of India

### A Crowdsourced Discovery Platform for Unexplored Destinations

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-blue?style=for-the-badge&logo=postgresql)](https://www.postgresql.org/)
[![JWT](https://img.shields.io/badge/JWT-Auth-orange?style=for-the-badge&logo=jsonwebtokens)](https://jwt.io/)
[![Android](https://img.shields.io/badge/Android-Client-green?style=for-the-badge&logo=android)](https://developer.android.com/)

*Discover. Explore. Share. — Unveiling the hidden treasures of India.*

---

[Features](#-features) • [Architecture](#-architecture) • [API Reference](#-api-reference) • [Getting Started](#-getting-started) • [Team](#-team)

</div>

---

## 🎯 The Problem

Northeast India & other Indian states are home to some of the most **breathtaking landscapes, rich cultures, and hidden destinations** — yet most remain **undiscovered** by mainstream tourism.

- 📍 **No centralized platform** exists for discovering offbeat locations
- 🗺️ **Scattered information** across blogs, social media, and word-of-mouth
- 📸 **Local gems remain hidden** — only known to residents
- 🚫 **No community-driven approach** to share authentic travel experiences

---

## 💡 Our Solution

**GONLY** is a **crowdsourced discovery platform** where users can:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                         │
│   🔍 DISCOVER        →   Find hidden gems on an interactive map        │
│   📍 SUBMIT          →   Add new locations with GPS, photos & tips     │
│   📸 CONTRIBUTE      →   Upload photos to existing gems (crowdsourced) │
│   ⭐ REVIEW          →   Rate and review places you've visited         │
│   🔖 SAVE            →   Bookmark gems for future trips                │
│   🎪 EXPLORE         →   Discover local events and festivals           │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## ✨ Features

### Core Features

| Feature | Description |
|---------|-------------|
| 🔐 **User Authentication** | Secure JWT-based registration & login |
| 🗺️ **Interactive Map** | Google Maps integration with gem markers |
| ➕ **Submit Hidden Gems** | Add locations with GPS auto-detect |
| 📸 **Crowdsourced Photos** | Any user can contribute photos to any gem |
| 🔍 **Smart Search** | Multi-strategy search with typo handling |
| ⭐ **Reviews & Ratings** | Community-driven ratings system |
| 🔖 **Bookmarks** | Save gems for future reference |
| 🎪 **Local Events** | Discover festivals and cultural events |
| 🏷️ **Categories & Filters** | Filter by Nature, Culture, Food, Adventure, etc. |

### Technical Highlights

- ✅ **RESTful API** design following best practices
- ✅ **Stateless Authentication** with JWT tokens
- ✅ **BYTEA Image Storage** in PostgreSQL
- ✅ **Cascading Search** — Name → Advanced → Multi-keyword → Fuzzy
- ✅ **Haversine Formula** for nearby gems calculation
- ✅ **Role-based Access** — ready for admin moderation

---

## 🏗️ Architecture

### High-Level Overview

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           GONLY ARCHITECTURE                            │
└─────────────────────────────────────────────────────────────────────────┘

                              ┌─────────────────┐
                              │   Android App   │
                              │   (Frontend)    │
                              └────────┬────────┘
                                       │
                                       │ HTTPS / REST API
                                       │ JWT Authentication
                                       ▼
┌───────────────────────────────────────────────────────────────────────┐
│                         SPRING BOOT BACKEND                           │
│                                                                       │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐               │
│  │ Controllers │───▶│  Services   │───▶│Repositories │               │
│  │   (REST)    │    │  (Logic)    │    │   (JPA)     │               │
│  └─────────────┘    └─────────────┘    └──────┬──────┘               │
│         │                                     │                       │
│         │           ┌─────────────┐           │                       │
│         └──────────▶│ JWT Filter  │           │                       │
│                     │ (Security)  │           │                       │
│                     └─────────────┘           │                       │
└───────────────────────────────────────────────┼───────────────────────┘
                                                │
                                                ▼
                              ┌─────────────────────────────┐
                              │        PostgreSQL           │
                              │         Database            │
                              │                             │
                              │  • users                    │
                              │  • hidden_gems              │
                              │  • gem_images (BYTEA)       │
                              │  • reviews                  │
                              │  • bookmarks                │
                              │  • local_events             │
                              │  • reports                  │
                              └─────────────────────────────┘
```

### Backend Package Structure

```
com.team_inertia.gonly/
│
├── 📁 config/                    # Security & JWT Configuration
│   ├── JWTFilter.java
│   └── MyConfig.java
│
├── 📁 controller/                # REST API Endpoints
│   ├── AuthController.java
│   ├── GemController.java
│   ├── ReviewController.java
│   ├── BookmarkController.java
│   ├── EventController.java
│   └── ReportController.java
│
├── 📁 dto/                       # Request/Response Objects
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   ├── GemRequest.java
│   ├── GemResponse.java
│   └── ...
│
├── 📁 enums/                     # Enumerations
│   ├── Category.java
│   ├── GemStatus.java
│   ├── DifficultyLevel.java
│   └── ...
│
├── 📁 model/                     # JPA Entities
│   ├── User.java
│   ├── HiddenGem.java
│   ├── GemImage.java
│   ├── Review.java
│   └── ...
│
├── 📁 repo/                      # JPA Repositories
│   ├── UserDetailsRepo.java
│   ├── HiddenGemRepository.java
│   └── ...
│
├── 📁 service/                   # Business Logic
│   ├── AuthService.java
│   ├── GemService.java
│   ├── ReviewService.java
│   └── ...
│
└── GonlyApplication.java         # Main Application
```

### Database Schema

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         DATABASE RELATIONSHIPS                          │
└─────────────────────────────────────────────────────────────────────────┘

    ┌──────────┐         ┌──────────────┐         ┌────────────┐
    │  users   │────────▶│  hidden_gems │◀────────│  reviews   │
    └──────────┘  1:N    └──────────────┘   N:1   └────────────┘
         │                      │                       │
         │                      │ 1:N                   │ 1:N
         │                      ▼                       ▼
         │               ┌──────────────┐        ┌─────────────┐
         │               │  gem_images  │        │review_images│
         │               └──────────────┘        └─────────────┘
         │
         │  1:N          ┌──────────────┐
         └──────────────▶│  bookmarks   │
         │               └──────────────┘
         │
         │  1:N          ┌──────────────┐         ┌─────────────┐
         └──────────────▶│ local_events │────────▶│event_images │
         │               └──────────────┘   1:N   └─────────────┘
         │
         │  1:N          ┌──────────────┐
         └──────────────▶│   reports    │
                         └──────────────┘
```

---

## 📡 API Reference

### Authentication

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `POST` | `/api/auth/register` | Register new user | ❌ |
| `POST` | `/api/auth/login` | Login & get JWT token | ❌ |
| `GET` | `/api/auth/profile` | Get current user profile | ✅ |

### Hidden Gems

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/gems` | Get all approved gems | ❌ |
| `GET` | `/api/gems/{id}` | Get gem by ID | ❌ |
| `GET` | `/api/gems/search?q=` | Smart search gems | ❌ |
| `GET` | `/api/gems/category?type=` | Filter by category | ❌ |
| `GET` | `/api/gems/state?name=` | Filter by state | ❌ |
| `GET` | `/api/gems/nearby?lat=&lng=` | Find nearby gems | ❌ |
| `POST` | `/api/gems` | Submit new gem | ✅ |
| `GET` | `/api/gems/my` | Get my submissions | ✅ |
| `DELETE` | `/api/gems/{id}` | Delete own gem | ✅ |

### Gem Images (Crowdsourced)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/gems/{gemId}/images` | Get all images metadata | ❌ |
| `GET` | `/api/gems/images/{imageId}` | Get image bytes | ❌ |
| `POST` | `/api/gems/{gemId}/images` | Upload image to gem | ✅ |

### Reviews

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/gems/{gemId}/reviews` | Get reviews for gem | ❌ |
| `POST` | `/api/gems/{gemId}/reviews` | Add review | ✅ |
| `DELETE` | `/api/gems/{gemId}/reviews/{id}` | Delete own review | ✅ |

### Bookmarks

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/bookmarks` | Get my bookmarks | ✅ |
| `POST` | `/api/bookmarks/{gemId}` | Toggle bookmark | ✅ |
| `GET` | `/api/bookmarks/check/{gemId}` | Check if bookmarked | ✅ |

### Events

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/events` | Get all events | ❌ |
| `GET` | `/api/events/upcoming` | Get upcoming events | ❌ |
| `POST` | `/api/events` | Submit new event | ✅ |

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- PostgreSQL 15+
- Maven 3.8+
- Android Studio (for mobile app)

### Backend Setup

```bash
# 1. Clone the repository
git clone https://github.com/RingkhangBTY/Gonly_backend_server
cd Gonly_backend_server

# 2. Configure database (application.properties)
spring.datasource.url=jdbc:postgresql://localhost:5432/gonly_db
spring.datasource.username=your_username
spring.datasource.password=your_password

# 3. Set JWT secret
jwt.secret=your-super-secret-key-min-32-characters

# 4. Run the application
./mvnw spring-boot:run
```

### Test the API

```bash
# Register a user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123","fullName":"Test User"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'
```

> 💡 **Tip:** You can also use [Postman](https://www.postman.com/) for easier API testing!

---

## 📊 Categories

Hidden gems are organized into:

| Category | Icon | Examples |
|----------|------|----------|
| `NATURE` | 🌿 | Waterfalls, Lakes, Forests |
| `CULTURE` | 🎭 | Villages, Traditions, Art |
| `FOOD` | 🍜 | Local Cuisine, Street Food |
| `ADVENTURE` | ⛰️ | Trekking, Camping, Caving |
| `HERITAGE` | 🏛️ | Monuments, Historic Sites |
| `WILDLIFE` | 🦋 | National Parks, Sanctuaries |

---

## 🔮 Future Roadmap

- [ ] 🔔 Push notifications for new gems nearby
- [ ] 🗺️ Offline map support
- [ ] 🤖 AI-powered gem recommendations
- [ ] 👨‍💼 Admin moderation dashboard
- [ ] 🌐 Multi-language support (Hindi, Regional languages)
- [ ] 📊 Travel itinerary generator
- [ ] 🏆 Gamification — badges for contributors

---

## 👥 Team

<div align="center">

### 🚀 Team Inertia

[//]: # ()
[//]: # (| Role | Member |)

[//]: # (|------|--------|)

[//]: # (| 💻 Backend Developer | Your Name |)

[//]: # (| 📱 Android Developer | Team Member |)

[//]: # (| 🎨 UI/UX Designer | Team Member |)

*Built with ❤️ for India's Hidden Treasures*

</div>

---

## 🤝 Contributing

We welcome contributions! Feel free to:

1. 🍴 Fork the repository
2. 🌿 Create a feature branch (`git checkout -b feature/amazing-feature`)
3. 💾 Commit your changes (`git commit -m 'Add amazing feature'`)
4. 📤 Push to the branch (`git push origin feature/amazing-feature`)
5. 🔃 Open a Pull Request

---

<div align="center">

### 🌟 Star this repo if you love exploring hidden gems!

---

**GONLY** — *Go Only Where Others Haven't*

<sub>Made with ☕ and 💻 by Team Inertia</sub>

</div>