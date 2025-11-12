# CORS 문제 해결을 위한 환경변수 설정 가이드

## 📋 변경 사항 요약

### 1. 코드 수정 완료 항목
- ✅ SecurityConfig: CORS 설정 개선 (버셀 도메인 명시 추가, CORS 헤더 노출)
- ✅ WebConfig: 중복 CORS 설정 제거 (SecurityConfig에서만 관리)
- ✅ CustomSuccessHandler: 기본 리다이렉트 포트 3000 → 5173 변경
- ✅ CustomFailureHandler: 기본 리다이렉트 포트 3000 → 5173 변경
- ✅ application.yml: 하드코딩된 Swagger URL 제거
- ✅ application-dev.yml: HTTPS Swagger URL로 변경
- ✅ application-prod.yml: Swagger 서버 URL 설정 추가

### 2. 주요 개선 사항
1. **CORS 설정 단일화**: SecurityConfig에서만 CORS를 관리하여 충돌 방지
2. **버셀 도메인 명시 추가**: `https://www.beautiq.my` 및 `https://*.beautiq.my` 패턴 추가
3. **CORS 헤더 노출**: 프론트에서 `Access-Control-Allow-Origin`, `Access-Control-Allow-Credentials` 확인 가능
4. **개발 환경 일치**: 모든 기본 포트를 5173으로 통일

---

## 🔧 AWS 환경변수 설정 필요 항목

### 필수 환경변수 (AWS에 설정 필요)

#### 1. 프론트엔드 오리진
```bash
FRONTEND_ORIGIN=https://www.beautiq.my
```
- **용도**: CORS 허용 도메인
- **현재 상태**: 환경변수로 관리 중
- **참고**: localhost는 코드에 하드코딩되어 있어 별도 설정 불필요

#### 2. OAuth 리다이렉트 URL
```bash
OAUTH_REDIRECT=https://www.beautiq.my/oauth/callback
```
- **용도**: OAuth2 로그인 성공/실패 후 리다이렉트 URL
- **현재 상태**: 환경변수로 관리 중

#### 3. 쿠키 도메인
```bash
COOKIE_DOMAIN=.beautiq.my
```
- **용도**: JWT 쿠키가 서브도메인에서도 작동하도록 설정
- **주의**: 앞에 `.` 붙여야 서브도메인 포함

#### 4. 쿠키 보안 플래그
```bash
COOKIE_SECURE=true
```
- **용도**: HTTPS에서만 쿠키 전송
- **프로덕션**: `true` 필수

#### 5. Swagger 서버 URL (선택사항)
```bash
SWAGGER_SERVER_URL=https://api.beautiq.my
```
- **용도**: Swagger UI에 표시될 서버 URL
- **참고**: 프로덕션에서 Swagger 비활성화 시 불필요

#### 6. AI 서버 URL
```bash
AI_SERVER_URL=http://<AI_서버_주소>:8000
```
- **용도**: AI 서버와 통신 (스타일 추천, 메이크업 시뮬레이션)
- **현재 에러**: `Connection refused: localhost/127.0.0.1:8000`
- **해결 방법**: AI 서버의 실제 주소로 변경 필요

---

## 🚀 AWS 환경변수 설정 방법

### AWS Elastic Beanstalk 사용 시
```bash
# AWS CLI 사용
aws elasticbeanstalk update-environment \
  --environment-name your-env-name \
  --option-settings \
    Namespace=aws:elasticbeanstalk:application:environment,OptionName=FRONTEND_ORIGIN,Value=https://www.beautiq.my \
    Namespace=aws:elasticbeanstalk:application:environment,OptionName=OAUTH_REDIRECT,Value=https://www.beautiq.my/oauth/callback \
    Namespace=aws:elasticbeanstalk:application:environment,OptionName=COOKIE_DOMAIN,Value=.beautiq.my \
    Namespace=aws:elasticbeanstalk:application:environment,OptionName=COOKIE_SECURE,Value=true \
    Namespace=aws:elasticbeanstalk:application:environment,OptionName=AI_SERVER_URL,Value=http://your-ai-server:8000
```

### AWS EC2 + Docker 사용 시
`.env` 파일 생성:
```bash
FRONTEND_ORIGIN=https://www.beautiq.my
OAUTH_REDIRECT=https://www.beautiq.my/oauth/callback
COOKIE_DOMAIN=.beautiq.my
COOKIE_SECURE=true
AI_SERVER_URL=http://your-ai-server:8000
SWAGGER_SERVER_URL=https://api.beautiq.my
```

`docker-compose.yml`에 이미 환경변수가 설정되어 있으므로 `.env` 파일만 생성하면 됩니다.

### AWS ECS 사용 시
Task Definition에서 환경변수 추가:
```json
{
  "environment": [
    {"name": "FRONTEND_ORIGIN", "value": "https://www.beautiq.my"},
    {"name": "OAUTH_REDIRECT", "value": "https://www.beautiq.my/oauth/callback"},
    {"name": "COOKIE_DOMAIN", "value": ".beautiq.my"},
    {"name": "COOKIE_SECURE", "value": "true"},
    {"name": "AI_SERVER_URL", "value": "http://your-ai-server:8000"}
  ]
}
```

---

## 🧪 로컬 테스트

### 1. 로컬에서 HTTPS 테스트
```bash
# application-dev.yml 활성화
export SPRING_PROFILES_ACTIVE=dev

# 로컬에서 HTTPS 사용 시 (자체 서명 인증서)
keytool -genkeypair -alias beautiq -keyalg RSA -keysize 2048 \
  -storetype PKCS12 -keystore keystore.p12 -validity 3650

# application-dev.yml에 추가
server:
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: your-password
    key-store-type: PKCS12
    key-alias: beautiq
  port: 8080
```

### 2. Swagger 접근
- 로컬: `https://localhost:8080/swagger-ui/index.html`
- AWS: `https://api.beautiq.my/swagger-ui/index.html`

---

## 🔍 문제 해결

### CORS 에러가 계속 발생할 경우

1. **브라우저 개발자 도구 확인**
   ```
   - Network 탭에서 실제 요청 Origin 확인
   - Response Headers에 Access-Control-Allow-Origin 있는지 확인
   ```

2. **서버 로그 확인**
   ```bash
   # CORS 관련 로그 확인
   tail -f /var/log/spring-boot.log | grep -i cors
   ```

3. **환경변수 확인**
   ```bash
   # AWS Elastic Beanstalk
   eb printenv
   
   # Docker
   docker exec -it beautiq-app env | grep FRONTEND
   ```

4. **버셀 도메인 확인**
   - 실제 배포된 도메인이 `www.beautiq.my`가 맞는지 확인
   - 만약 다르다면 `FRONTEND_ORIGIN` 환경변수 수정 필요

### AI 서버 연결 에러 해결

현재 에러: `Connection refused: localhost/127.0.0.1:8000`

**원인**: AI 서버가 localhost:8000에서 실행되지 않음

**해결 방법**:
1. AI 서버의 실제 주소 확인
2. `AI_SERVER_URL` 환경변수를 실제 주소로 변경
3. 네트워크 보안 그룹에서 8000 포트 허용 확인

---

## 📝 체크리스트

### 배포 전
- [ ] 모든 환경변수 설정 완료
- [ ] AI 서버 URL 확인 및 설정
- [ ] HTTPS 인증서 설정 (프로덕션)
- [ ] 버셀 도메인 확인

### 배포 후
- [ ] CORS 헤더 정상 응답 확인
- [ ] OAuth2 로그인 테스트
- [ ] 쿠키 전송 확인 (Credentials: include)
- [ ] Swagger UI 접근 확인
- [ ] AI 서버 연결 확인

---

## 📞 추가 지원

문제가 계속되면 다음 정보를 확인해주세요:
1. 브라우저 콘솔 에러 메시지 전체
2. 서버 로그 (CORS 관련 부분)
3. 실제 요청 URL과 Origin 헤더
4. AWS 환경변수 설정 스크린샷

