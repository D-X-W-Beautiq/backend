# Beautiq Backend

AI 기반 피부 분석 및 맞춤형 화장품 추천 서비스 백엔드 API

## 🚀 빠른 시작

### 1. 환경 설정

```bash
# .env 파일 생성
cp .env.example .env

# .env 파일 수정 (필수 값만)
# - DB_URL, DB_USERNAME, DB_PASSWORD
# - JWT_SECRET
# - AWS 자격증명
# - OAuth2 자격증명 (Google, Kakao)
```

**⚠️ 중요**: 로컬 개발 시 쿠키 인증을 사용하려면 프론트엔드도 **HTTPS로 실행**해야 합니다.
- 자세한 설정: [LOCAL_HTTPS_SETUP.md](./LOCAL_HTTPS_SETUP.md)

### 2. 실행

```bash
# Gradle로 실행
./gradlew bootRun

# 또는 빌드 후 실행
./gradlew build
java -jar build/libs/Beautiq-1.0.0-SNAPSHOT.jar
```

### 3. 접속

- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html

## 📋 주요 기능

- **피부 분석**: AI 기반 얼굴 이미지 분석 및 피부 상태 진단
- **제품 추천**: 피부 분석 결과 기반 맞춤형 화장품 추천
- **메이크업 시뮬레이션**: AI 메이크업 미리보기 및 커스터마이징
- **사용자 관리**: OAuth2 소셜 로그인 (Google, Kakao)
- **위시리스트**: 관심 제품 저장 및 관리

## 🛠 기술 스택

- **Framework**: Spring Boot 3.x
- **Language**: Java 17
- **Database**: MySQL 8.0
- **Authentication**: JWT + OAuth2
- **Cloud**: AWS S3
- **Documentation**: Swagger/OpenAPI 3.0
- **Build**: Gradle

## 📁 프로젝트 구조

```
src/
├── main/
│   ├── java/spring/beautiq/
│   │   ├── domain/           # 도메인별 패키지
│   │   │   ├── auth/         # 인증/인가
│   │   │   ├── makeup/       # 메이크업
│   │   │   ├── product/      # 제품
│   │   │   ├── skin/         # 피부 분석
│   │   │   └── user/         # 사용자
│   │   └── global/           # 전역 설정
│   │       ├── config/       # 설정 클래스
│   │       └── jwt/          # JWT 유틸리티
│   └── resources/
│       ├── application.yml   # 설정 파일 (단일)
│       └── .env             # 환경변수 (Git 제외)
```

## 🌍 환경 설정

### 개발 환경
`.env` 파일에 개발 환경 설정:
```bash
FRONTEND_ORIGIN=http://localhost:5173
COOKIE_SECURE=false
SWAGGER_ENABLED=true
JPA_DDL_AUTO=update
```

### 프로덕션 환경
`.env` 파일을 프로덕션 설정으로 변경:
```bash
FRONTEND_ORIGIN=https://www.beautiq.my
COOKIE_SECURE=true
SWAGGER_ENABLED=false
JPA_DDL_AUTO=validate
```

**자세한 환경변수 목록**: [ENVIRONMENT_VARIABLES.md](./ENVIRONMENT_VARIABLES.md)

## 🐳 Docker

### Docker Compose 사용
```bash
# 빌드 및 실행
docker-compose up -d

# 로그 확인
docker logs -f beautiq-backend

# 중지
docker-compose down
```

### Dockerfile로 직접 빌드
```bash
# 이미지 빌드
docker build -t beautiq-backend:latest .

# 컨테이너 실행
docker run -d \
  --name beautiq-backend \
  --env-file .env \
  -p 8080:8080 \
  beautiq-backend:latest
```

## 📚 API 문서

### Swagger UI
개발 환경에서 Swagger UI를 통해 API를 테스트할 수 있습니다:
- URL: http://localhost:8080/swagger-ui.html
- 인증: JWT Bearer Token 사용

### 주요 엔드포인트

#### 인증
- `POST /users/login` - 개발용 로그인
- `GET /oauth2/authorization/{provider}` - OAuth2 로그인 시작

#### 피부 분석
- `POST /skin-analysis` - 피부 분석 요청
- `GET /skin-analysis/{id}` - 분석 결과 조회
- `GET /skin-analysis/trends` - 피부 트렌드 조회

#### 제품
- `GET /products` - 제품 목록 조회
- `GET /products/{id}` - 제품 상세 조회
- `GET /products/recommendations` - 맞춤 추천

#### 메이크업
- `POST /makeups` - 메이크업 생성
- `GET /makeups/{id}` - 메이크업 조회
- `POST /makeups/{id}/customize` - 메이크업 커스터마이징

#### 사용자
- `GET /users/me` - 내 정보 조회
- `GET /users/me/wishlist` - 위시리스트 조회

## 🔧 트러블슈팅

### Swagger에서 HTTP로 요청이 가는 경우
1. `.env`에서 `SWAGGER_SERVER_URL=https://api.beautiq.my` 확인
2. Nginx에서 `X-Forwarded-Proto` 헤더 전달 확인

### CORS 에러
1. `.env`에서 `FRONTEND_ORIGIN` 확인
2. 프론트엔드 도메인이 정확한지 확인

### 쿠키가 전달되지 않는 경우
1. HTTPS 환경: `COOKIE_SECURE=true` 확인
2. 서브도메인 공유: `COOKIE_DOMAIN=.beautiq.my` 확인

## 📖 문서

- [환경변수 설정 가이드](./ENVIRONMENT_VARIABLES.md)
- [로컬 HTTPS 개발 환경 설정](./LOCAL_HTTPS_SETUP.md) ⭐
- [변경사항 요약](./CHANGES_SUMMARY.md)

## 👥 팀

Beautiq Development Team

## 📄 라이선스

Copyright (c) 2025 Beautiq Team

