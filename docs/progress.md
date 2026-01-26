# OrderPing 개발 진행사항

## 프로젝트 개요

주점/식당 주문 관리 시스템 - Spring Boot 기반 백엔드 API

---

## 모듈 구조

```
orderping/
├── orderping-api        # REST API, 컨트롤러, 서비스 (Spring Boot 3.5)
├── orderping-domain     # 순수 도메인 모델, 리포지토리 인터페이스
├── orderping-infra      # JPA 엔티티, 리포지토리 구현체
└── orderping-external   # 외부 연동 (현재 비어있음)
```

## 기술 스택

- Java 17
- Spring Boot 3.5.x
- Spring Data JPA
- Spring Security + JWT
- OAuth2 (Kakao)
- MySQL 8.x (개발), H2 (테스트)
- AWS S3 (Presigned URL)
- Springdoc OpenAPI 2.7.0
- Lombok 1.18.30
- Gradle 멀티모듈
- Docker / Docker Compose

---

## 도메인 모델

| 도메인 | 설명 |
|--------|------|
| User | 사용자 (Owner) |
| AuthAccount | 소셜 로그인 계정 (Kakao) |
| RefreshToken | JWT Refresh Token (DB 저장) |
| Store | 주점 |
| StoreTable | 테이블 |
| StoreAccount | 주점 계좌 정보 (암호화 저장) |
| Category | 메뉴 카테고리 |
| Menu | 메뉴 |
| Order / OrderMenu | 주문 |
| Payment | 결제 |
| Bank | 은행 정보 (토스 딥링크용) |

---

## API 엔드포인트

| 경로 | 설명 |
|------|------|
| `/api/auth` | 인증 관리 (토큰 재발급, 로그아웃) |
| `/api/users` | 사용자 관리 |
| `/api/main` | 메인 페이지 정보 |
| `/api/stores` | 주점 관리 |
| `/api/stores/accounts` | 주점 계좌 관리 |
| `/api/tables` | 테이블 관리 |
| `/api/categories` | 카테고리 관리 |
| `/api/menus` | 메뉴 관리 |
| `/api/orders` | 주문 관리 |
| `/api/payments` | 결제 관리 |
| `/api/images` | 이미지 업로드 (Presigned URL) |
| `/api/qr` | QR 코드 / 토스 딥링크 |
| `/api/banks` | 은행 목록 조회 |

---

## 개발 히스토리

### PR #1: 멀티모듈 구조 초기 구현 (2025-12-18)

**브랜치**: `feat/multi-module`

**작업 내용**:
- Gradle 멀티모듈 프로젝트 구조 생성
- Docker / Docker Compose 설정
- 기본 프로젝트 세팅

---

### PR #2: 도메인 모듈 구현 (2025-12-19)

**브랜치**: `feat/domain-module`

**작업 내용**:
- 도메인 클래스 생성
  - User, AuthAccount, Store, StoreTable, StoreAccount
  - Category, Menu, Order, OrderMenu, Payment
- 도메인 리포지토리 인터페이스 정의
- Enum 타입 정의 (Role, AuthProvider, OrderStatus, PaymentMethod, PaymentStatus, TableStatus)
- 도메인 테스트 코드 작성

---

### PR #3: 인프라 모듈 구현 (2025-12-19)

**브랜치**: `feat/infra-module`

**작업 내용**:
- JPA 엔티티 생성 (모든 도메인에 대응)
- JPA 리포지토리 구현체 생성
- 도메인-엔티티 변환 로직
- 인프라 모듈 테스트 코드

---

### PR #4: Claude PR 리뷰 봇 도입 (2025-12-25)

**브랜치**: `feat/claude-pr-review-bot`

**작업 내용**:
- GitHub Action으로 Claude 코드 리뷰 자동화
- PR 생성 시 자동 리뷰 댓글

---

### PR #5: API 모듈 구현 (2025-12-28)

**브랜치**: `feat/api-module`

**작업 내용**:
- REST API 컨트롤러 생성 (User, Store, Menu, Order, Payment 등)
- 서비스 레이어 구현
- DTO 클래스 생성
- **계좌 암호화 추가** (AES 암호화)
- **커스텀 예외 처리** (NotFoundException, ForbiddenException, BadRequestException 등)
- 전역 예외 핸들러 (GlobalExceptionHandler)
- Swagger 문서 설정

---

### PR #6: Presigned URL 기능 (2025-12-29)

**브랜치**: `feat/presigned-url`

**작업 내용**:
- AWS S3 Presigned URL 발급 API
- 이미지 업로드 기능 구현
- 파일 크기 제한 응답 추가
- 입력 검증 로직 추가

**API**:
```
POST /api/images/presigned-url
- 요청: { "fileName": "image.jpg", "contentType": "image/jpeg" }
- 응답: { "presignedUrl": "https://...", "fileUrl": "https://..." }
```

---

### PR #7: Kakao OAuth 구현 (2025-12-30 ~ 2025-12-31)

**브랜치**: `feat/kakao-oauth`

**작업 내용**:
- Spring Security + OAuth2 설정
- Kakao 소셜 로그인 연동
- JWT Access Token / Refresh Token 구현
- Refresh Token DB 저장 방식
- OAuth2 성공/실패 핸들러 분리
- 토큰 만료시간 설정 (Access: 15분, Refresh: 7일)

**인증 흐름**:
```
1. GET /oauth2/authorization/kakao → 카카오 로그인 페이지
2. 카카오 인증 완료 → /login/oauth2/code/kakao 콜백
3. OAuth2AuthenticationSuccessHandler에서 JWT 발급
4. 프론트엔드로 리다이렉트 (토큰 전달)
```

---

### PR #8: Docker 포트 수정 (2025-12-31)

**브랜치**: `fix/docker-port`

**작업 내용**:
- docker-compose 포트번호 수정

---

### PR #9: CORS 설정 (2025-12-31)

**브랜치**: `feat/cors`

**작업 내용**:
- CORS 기본 설정 추가
- 프론트엔드 도메인 허용

---

### PR #10: 카카오 콜백 URI 변경 (2026-01-02)

**브랜치**: `feat/callback-uri`

**작업 내용**:
- 카카오 OAuth 리다이렉트 URI 변경

---

### PR #11: 도커 컴포즈 리다이렉트 URI 변경 (2026-01-02)

**브랜치**: `fix/redirect-uri`

**작업 내용**:
- 도커 컴포즈 환경변수 카카오 리다이렉트 URI 수정

---

### PR #12: 토스 딥링크 기능 (2026-01-15)

**브랜치**: `feat/toss-deeplink`

**작업 내용**:
- 토스 송금 딥링크 생성 기능
- QR 코드 URL 생성
- 은행 정보 관리 (Bank 도메인)
- 계좌 등록 중복 검사 로직
- Security 세팅 추가

**API**:
```
GET /api/qr/toss-deeplink?storeId=1&amount=10000
- 응답: 토스앱 딥링크 URL

GET /api/banks
- 응답: 은행 목록 (토스 지원 은행)
```

---

### PR #13: storeId + categoryId 복합 조회 (2026-01-16)

**브랜치**: `feat/getby-category&store`

**작업 내용**:
- 메뉴 조회 API 통합 (Ambiguous handler 에러 해결)
- `findByStoreIdAndCategoryId` 메서드 추가
- 복합 쿼리 파라미터 지원

**문제 상황**:
```java
// 기존 - 충돌 발생
@GetMapping(params = "storeId")
@GetMapping(params = "categoryId")
```

**해결**:
```java
// 변경 - 단일 엔드포인트
@GetMapping
public ResponseEntity<List<MenuResponse>> getMenus(
    @RequestParam(required = false) Long storeId,
    @RequestParam(required = false) Long categoryId
)
```

**API**:
```
GET /api/menus?storeId=1
GET /api/menus?categoryId=1
GET /api/menus?storeId=1&categoryId=2  # 신규
```

---

### PR #14: 토큰 헤더 방식 변경 (2026-01-16)

**브랜치**: `fix/token-header`

**작업 내용**:
- JWT 토큰 전달 방식 개선
- GitHub Action 삭제된 파일 에러 수정

---

### 현재 작업: RefreshToken 보안 강화 (2026-01-20)

**브랜치**: `fix/token-header` (계속)

#### 1. JWT Access Token 만료시간 변경

- 15분 → 10시간 (개발 편의성)
- 사용자가 추후 10년으로 변경 가능

#### 2. RefreshToken HttpOnly 쿠키 방식 전환

**문제**: RefreshToken이 응답 바디에 노출되면 XSS 공격에 취약

**해결**: RefreshToken을 HttpOnly 쿠키로만 전달

**변경 파일**:

| 파일 | 변경 내용 |
|------|----------|
| `OAuth2AuthenticationSuccessHandler.java` | refreshToken을 쿠키로 설정, URL에는 accessToken만 |
| `AuthService.java` | `TokenPair` record 추가 (내부용), `refreshTokens()`는 accessToken만 반환 |
| `TokenResponse.java` | refreshToken 필드 제거 |
| `AuthController.java` | `@CookieValue`로 refreshToken 수신 |
| `TokenRefreshRequest.java` | 삭제 |
| `LogoutRequest.java` | 삭제 |

**최종 JWT 인증 흐름**:

```
[OAuth2 로그인]
1. 카카오 로그인 완료
2. OAuth2AuthenticationSuccessHandler:
   - AccessToken 생성
   - RefreshToken 생성 → DB 저장 + HttpOnly 쿠키 설정
3. 리다이렉트: /callback?accessToken=xxx
   (RefreshToken은 Set-Cookie 헤더로 자동 설정)

[API 호출]
1. Authorization: Bearer {accessToken} 헤더
2. JwtAuthenticationFilter에서 검증
3. 만료 시 401 응답

[토큰 재발급 - POST /api/auth/refresh]
1. 브라우저가 쿠키 자동 전송
2. @CookieValue로 refreshToken 추출
3. 검증 후 새 AccessToken 발급
4. 응답: { "accessToken": "new_token" }

[로그아웃 - POST /api/auth/logout]
1. 브라우저가 쿠키 자동 전송
2. DB에서 RefreshToken 삭제
3. Set-Cookie로 쿠키 만료 처리 (maxAge=0)
```

**보안 특성**:

| 토큰 | 저장 위치 | 전송 방식 | XSS | CSRF |
|------|----------|----------|-----|------|
| AccessToken | 메모리 | Authorization 헤더 | 안전 | 안전 |
| RefreshToken | HttpOnly 쿠키 | 자동 (쿠키) | 안전 | SameSite로 보호 |

#### 3. StoreTable 조회 API 통합

Menu API와 동일한 패턴 적용

**API**:
```
GET /api/tables?storeId=1
GET /api/tables?storeId=1&status=ACTIVE
```

---

## 구현 상태 요약

| 기능 | 상태 |
|------|------|
| 멀티모듈 구조 | ✅ 완료 |
| 도메인 모델 | ✅ 완료 |
| JPA 엔티티/리포지토리 | ✅ 완료 |
| REST API (CRUD) | ✅ 완료 |
| Swagger 문서 | ✅ 완료 |
| JWT 인증 | ✅ 완료 |
| OAuth2 (Kakao) | ✅ 완료 |
| RefreshToken (쿠키) | ✅ 완료 |
| 전역 예외 처리 | ✅ 완료 |
| 계좌 암호화 | ✅ 완료 |
| Presigned URL | ✅ 완료 |
| 토스 딥링크 | ✅ 완료 |
| CORS 설정 | ✅ 완료 |
| Claude PR 리뷰 | ✅ 완료 |
| 모니터링 (Prometheus/Loki/Grafana) | ✅ 완료 |
| 테이블 단체 생성/삭제 | ✅ 완료 |
| QR 지속성 | ✅ 완료 |
| Bean Validation | ⏳ 미구현 |
| 카카오 알림톡 | ⏳ 미구현 |

---

## 다음 작업 예정

- [ ] Bean Validation 추가 (입력 검증)
- [ ] 카카오 알림톡 연동
- [ ] 주문 실시간 알림 (WebSocket/SSE)
- [ ] 테스트 코드 보강

---

## 환경 설정

### 로컬 개발

```bash
# Docker로 MySQL 실행
docker compose up -d

# 애플리케이션 실행
./gradlew :orderping-api:bootRun
```

### 환경변수

```yaml
# application.yml 주요 설정
jwt:
  secret: ${JWT_SECRET}
  access-token-expiration: 36000000  # 10시간
  refresh-token-expiration: 604800000  # 7일

oauth2:
  frontend-url: ${FRONTEND_URL:http://localhost:5173}

spring:
  security:
    oauth2:
      client:
        registration:
          kakao:
            client-id: ${KAKAO_CLIENT_ID}
            client-secret: ${KAKAO_CLIENT_SECRET}

cloud:
  aws:
    s3:
      bucket: ${S3_BUCKET}
    credentials:
      access-key: ${AWS_ACCESS_KEY}
      secret-key: ${AWS_SECRET_KEY}
```

---

## Claude 사용 가이드

> **중요**: 새 Claude 세션 시작 시 이 파일을 먼저 읽고 문맥을 파악하세요.

### 작업 완료 후 해야 할 일

1. **이 파일 업데이트**: 작업 완료 후 아래 "작업 로그" 섹션에 내용 추가
2. **형식**: 날짜, 작업 내용, 변경 파일, 주요 결정사항 기록
3. **구현 상태 요약**: 새 기능 완료 시 체크리스트 업데이트

---

## 작업 로그

### 2026-01-26

**작업 내용**:

#### 1. 모니터링 환경 구축
- Prometheus + Loki + Grafana 모니터링 스택 설정
- 모니터링 서버(43.201.31.24)와 orderping 서버 분리 구성
- Spring Boot Actuator + Micrometer Prometheus 연동
- Promtail로 로그 수집 → Loki 전송

**모니터링 구성**:
```
모니터링 서버 (43.201.31.24)
├── Prometheus (:9090) - 메트릭 수집
├── Loki (:3100) - 로그 저장
└── Grafana (:3000) - 대시보드

Orderping 서버
├── API (:8085) - /actuator/prometheus 엔드포인트 노출
└── Promtail - 로그 수집 → Loki 전송
```

#### 2. Actuator 보안 설정
- `/actuator/health`, `/actuator/prometheus`만 공개
- 나머지 `/actuator/**`는 ADMIN 권한 필요

#### 3. 테이블 단체 삭제 기능
- `DELETE /api/tables/bulk` API 추가
- 주문이 있는 테이블은 삭제 불가 (400 에러)

**API**:
```json
DELETE /api/tables/bulk
{
  "storeId": 1,
  "tableNums": [1, 2, 3]
}
```

#### 4. 로그아웃 방식 변경
- 쿠키 → 헤더 방식으로 변경
- `X-Refresh-Token` 헤더로 refreshToken 전달

#### 5. QR 코드 지속성 개선
- `clearTable` 시 QR URL 유지되도록 수정
- QR 토큰 조회: `tableId` → `storeId + tableNum`으로 변경
- 테이블 갈아끼워도 같은 QR 계속 사용 가능

#### 6. 테이블 생성 시 QR URL 지원
- `POST /api/tables` 요청에 `qrImageUrl` 필드 추가 (선택)

**변경 파일**:
- `SecurityConfig.java` - Actuator 엔드포인트 보안 설정
- `AuthController.java` - 로그아웃 헤더 방식 변경
- `StoreTableService.java` - 단체 삭제, clearTable QR 보존
- `StoreTableCreateRequest.java` - qrImageUrl 필드 추가
- `StoreTableBulkDeleteRequest.java` - 신규
- `TableQrService.java` - 활성 테이블 조회 방식 변경
- `StoreTableRepository.java` - findActiveByStoreIdAndTableNum 추가
- `docker/docker-compose.yml` - 모니터링 스택 제거 (별도 서버)
- `docker/monitoring/promtail-config.yml` - Loki 전송 설정

**주요 결정**:
- 모니터링은 별도 서버에서 운영
- QR은 물리적 테이블 번호 기준으로 지속
- 주문 있는 테이블 삭제 차단

---

### 2026-01-20

**작업 내용**:
- RefreshToken HttpOnly 쿠키 방식 전환
- JWT Access Token 만료시간 10시간으로 변경
- StoreTable/Menu 조회 API 통합
- 불필요한 DTO 삭제 (TokenRefreshRequest, LogoutRequest)

**변경 파일**:
- `AuthService.java` - TokenPair record 추가
- `AuthController.java` - @CookieValue 방식으로 변경
- `OAuth2AuthenticationSuccessHandler.java` - 쿠키 설정 추가
- `TokenResponse.java` - refreshToken 필드 제거

**주요 결정**:
- RefreshToken은 절대 응답 바디에 노출하지 않음
- HttpOnly + Secure + SameSite=None 쿠키 사용

---

<!--
새 작업 추가 시 아래 형식으로 작성:

### YYYY-MM-DD

**작업 내용**:
-

**변경 파일**:
-

**주요 결정**:
-

-->
