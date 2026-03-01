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
└── orderping-external   # 외부 연동 (AWS S3, Discord 웹훅)
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

| 도메인               | 설명                        |
|-------------------|---------------------------|
| User              | 사용자 (Owner)               |
| AuthAccount       | 소셜 로그인 계정 (Kakao)         |
| RefreshToken      | JWT Refresh Token (DB 저장) |
| Store             | 주점                        |
| StoreTable        | 테이블                       |
| StoreAccount      | 주점 계좌 정보 (암호화 저장)         |
| Category          | 메뉴 카테고리                   |
| Menu              | 메뉴                        |
| Order / OrderMenu | 주문                        |
| Payment           | 결제                        |
| Bank              | 은행 정보 (토스 딥링크용)           |

---

## API 엔드포인트

### 운영자용 API (인증 필요)

| 경로                     | 설명                      |
|------------------------|-------------------------|
| `/api/auth`            | 인증 관리 (토큰 재발급, 로그아웃)    |
| `/api/users`           | 사용자 관리                  |
| `/api/main`            | 메인 페이지 정보               |
| `/api/stores`          | 주점 관리                   |
| `/api/stores/accounts` | 주점 계좌 관리                |
| `/api/tables`          | 테이블 관리                  |
| `/api/categories`      | 카테고리 관리                 |
| `/api/menus`           | 메뉴 관리                   |
| `/api/orders`          | 주문 관리 (매장 기준)           |
| `/api/payments`        | 결제 관리                   |
| `/api/images`          | 이미지 업로드 (Presigned URL) |

### 고객용 API (인증 불필요)

| 경로                     | 설명                  |
|------------------------|---------------------|
| `/api/customer/orders`        | 고객 주문 (생성, 테이블별 조회)         |
| `/api/customer/stores/{id}`   | 메뉴 목록 조회 (?tableNum=)         |
| `/api/customer/qr`            | QR 코드로 테이블 정보 조회            |
| `/api/customer/banks`         | 은행 목록 조회 (토스 딥링크용)          |

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

| 파일                                        | 변경 내용                                                           |
|-------------------------------------------|-----------------------------------------------------------------|
| `OAuth2AuthenticationSuccessHandler.java` | refreshToken을 쿠키로 설정, URL에는 accessToken만                        |
| `AuthService.java`                        | `TokenPair` record 추가 (내부용), `refreshTokens()`는 accessToken만 반환 |
| `TokenResponse.java`                      | refreshToken 필드 제거                                              |
| `AuthController.java`                     | `@CookieValue`로 refreshToken 수신                                 |
| `TokenRefreshRequest.java`                | 삭제                                                              |
| `LogoutRequest.java`                      | 삭제                                                              |

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

| 토큰           | 저장 위치       | 전송 방식            | XSS | CSRF         |
|--------------|-------------|------------------|-----|--------------|
| AccessToken  | 메모리         | Authorization 헤더 | 안전  | 안전           |
| RefreshToken | HttpOnly 쿠키 | 자동 (쿠키)          | 안전  | SameSite로 보호 |

#### 3. StoreTable 조회 API 통합

Menu API와 동일한 패턴 적용

**API**:

```
GET /api/tables?storeId=1
GET /api/tables?storeId=1&status=ACTIVE
```

---

## 구현 상태 요약

| 기능                             | 상태    |
|--------------------------------|-------|
| 멀티모듈 구조                        | ✅ 완료  |
| 도메인 모델                         | ✅ 완료  |
| JPA 엔티티/리포지토리                  | ✅ 완료  |
| REST API (CRUD)                | ✅ 완료  |
| Swagger 문서                     | ✅ 완료  |
| JWT 인증                         | ✅ 완료  |
| OAuth2 (Kakao)                 | ✅ 완료  |
| RefreshToken (쿠키)              | ✅ 완료  |
| 전역 예외 처리                       | ✅ 완료  |
| 계좌 암호화                         | ✅ 완료  |
| Presigned URL                  | ✅ 완료  |
| 토스 딥링크                         | ✅ 완료  |
| CORS 설정                        | ✅ 완료  |
| Claude PR 리뷰                   | ✅ 완료  |
| 모니터링 (Prometheus/Loki/Grafana) | ✅ 완료  |
| 테이블 단체 생성/삭제                   | ✅ 완료  |
| 테이블 QR 일괄 업데이트                 | ✅ 완료  |
| QR 지속성                         | ✅ 완료  |
| Access Log (Tomcat)            | ✅ 완료  |
| Swagger 그룹 분리 (운영자/고객)         | ✅ 완료  |
| 고객용 주문 API                     | ✅ 완료  |
| Bean Validation                | ✅ 완료  |
| Discord 웹훅 알림                  | ✅ 완료  |
| 주문 취소 시 재고 복구                  | ✅ 완료  |
| QR storeId+tableNum 기반 지속성     | ✅ 완료  |
| TableResolverService           | ✅ 완료  |
| 테이블 QR URL 일괄 조회               | ✅ 완료  |
| 카카오 알림톡                        | ⏳ 미구현 |

---

## 다음 작업 예정

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

discord:
  webhook-url: ${DISCORD_WEBHOOK_URL}
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

#### 7. 테이블 중복 생성 방지

- 활성 테이블 중 동일한 `store_id + table_num` 조합 존재 시 생성 차단
- MySQL 복합 unique 제약조건 대신 애플리케이션 레벨 검증
- `createStoreTable`, `createStoreTablesBulk` 모두 적용

**변경 파일**:

- `SecurityConfig.java` - Actuator 엔드포인트 보안 설정
- `AuthController.java` - 로그아웃 헤더 방식 변경
- `StoreTableService.java` - 단체 삭제, clearTable QR 보존, 중복 테이블 방지
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
- 복합 unique 제약조건 대신 애플리케이션 레벨 중복 검증 (clearTable 로직 때문)

---

### 2026-01-28

**작업 내용**:

#### 1. Tomcat Access Log 설정

- HTTP 요청 로그를 파일로 기록
- `/var/log/orderping/access.log`에 저장
- 클라이언트 IP, 요청 메서드, URI, 쿼리 스트링, 응답 시간 포함

**패턴**:

```
%{yyyy-MM-dd HH:mm:ss}t [%{X-Forwarded-For}i] %s %m %U%q %Dms
```

**예시 출력**:

```
2026-01-28 14:30:45 [123.45.67.89] 200 GET /api/tables?storeId=1 45ms
```

#### 2. Docker 컨테이너 시간대 설정

- API 컨테이너에 `TZ: Asia/Seoul` 환경변수 추가
- 로그 타임스탬프가 한국 시간으로 표시

#### 3. 테이블 QR 일괄 업데이트 API

- `PATCH /api/tables/bulk/{storeId}` 엔드포인트 추가
- 여러 테이블의 QR 이미지 URL을 한 번에 업데이트

**API**:

```json
PATCH /api/tables/bulk/{storeId}
{
  "updates": [
    { "tableId": 1, "qrImageUrl": "https://s3.../qr1.png" },
    { "tableId": 2, "qrImageUrl": "https://s3.../qr2.png" }
  ]
}
```

#### 4. 주문 조회 API 통합

- 기존 두 개의 엔드포인트를 하나로 통합
- `status` 파라미터를 선택적으로 사용

**API**:

```
GET /api/orders?storeId=1           → 해당 매장의 모든 주문
GET /api/orders?storeId=1&status=PENDING  → PENDING 상태만
```

#### 5. 주문에 tableNum 필드 추가

- Order 도메인, 엔티티, DTO에 `tableNum` 필드 추가
- 주문 생성 시 `tableNum`을 함께 저장
- 조회 시 테이블 조인 없이 바로 `tableNum` 반환

**주문 생성 요청**:

```json
POST /api/orders
{
  "tableId": 1,
  "tableNum": 3,
  "storeId": 1,
  ...
}
```

**변경 파일**:

- `application.yml` - Tomcat access log 설정 추가
- `docker-compose.yml` - TZ 환경변수 추가
- `StoreTableBulkQrUpdateRequest.java` - 신규 DTO
- `StoreTableService.java` - `updateStoreTableQrBulk` 메서드 추가
- `StoreTableController.java` - QR 일괄 업데이트 엔드포인트 추가
- `StoreTableApi.java` - Swagger 문서 추가
- `OrderController.java` - 주문 조회 API 통합
- `OrderService.java` - `getOrdersByStore` 메서드로 통합
- `OrderApi.java` - Swagger 문서 통합
- `Order.java` (domain) - `tableNum` 필드 추가
- `OrderEntity.java` (infra) - `table_num` 컬럼 추가
- `OrderCreateRequest.java` - `tableNum` 필드 추가
- `OrderResponse.java` - `tableNum` 필드 추가

#### 6. Swagger 문서 그룹 분리

- 운영자용 API와 고객용 API를 Swagger에서 분리
- `/swagger-ui.html` 접속 시 드롭다운으로 선택 가능

**그룹**:

- `1. 운영자용 API`: `/api/**` (고객용 제외)
- `2. 고객용 API`: `/api/customer/**`, `/api/qr/**`, `/api/banks/**`

#### 7. 고객용 주문 API 분리

- 고객 주문 API를 `/api/customer/orders`로 분리
- 테이블별 주문 조회 시 메뉴 상세 정보 포함

**고객용 API**:

```
POST /api/customer/orders              → 주문 생성
GET  /api/customer/orders/table/{id}   → 테이블 주문 내역 (메뉴 포함)
GET  /api/customer/qr/tables/{token}   → QR 토큰으로 테이블 정보 조회
GET  /api/customer/banks               → 은행 목록 조회
```

**응답 예시** (메뉴 포함):

```json
{
  "id": 1,
  "tableId": 1,
  "tableNum": 3,
  "status": "PENDING",
  "totalPrice": 25000,
  "menus": [
    { "menuId": 1, "menuName": "소주", "quantity": 2, "price": 5000, "isService": false },
    { "menuId": 2, "menuName": "삼겹살", "quantity": 1, "price": 15000, "isService": false }
  ]
}
```

**추가 변경 파일**:

- `SwaggerConfig.java` - GroupedOpenApi 설정 추가
- `CustomerOrderController.java` - 신규 (고객용 주문 컨트롤러)
- `OrderDetailResponse.java` - 신규 (메뉴 포함 응답 DTO)
- `OrderService.java` - `getOrdersWithMenusByTableId` 메서드 추가
- `SecurityConfig.java` - `/api/customer/**` permitAll 추가

**주요 결정**:

- Access log는 Promtail이 자동 수집 (*.log 패턴)
- Promtail에서 `/actuator`, `/swagger-ui`, `/v3/api-docs` 경로는 drop
- 주문에 `tableNum`을 직접 저장하여 조회 시 성능 향상
- 운영자/고객 API 분리로 Swagger 가독성 향상
- 고객용 주문 조회에만 메뉴 상세 포함 (성능 고려)

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

### 2026-01-29

**작업 내용**:

#### 1. N+1 쿼리 최적화

- `toOrderDetailResponse`에서 메뉴 조회 시 N+1 쿼리 문제 수정
- `MenuRepository.findAllByIds()` 배치 조회 메서드 추가

#### 2. 주문 생성 시 테이블 검증 추가

- `tableId`와 `tableNum` 일관성 검증
- 존재하지 않는 테이블이나 번호 불일치 시 에러 반환

#### 3. 주문 상세 조회 API 개선

- `GET /api/orders/{id}` 응답에 메뉴 정보 포함
- `OrderResponse` → `OrderDetailResponse`로 변경

#### 4. 주문 조회 API 충돌 해결

- `getOrdersByStore`와 `getOrdersByTableId` 간 Ambiguous handler 에러 수정
- 테이블별 조회는 고객용 API(`/api/customer/orders/table/{tableId}`)로 통합

#### 5. 로그아웃 API 개선

- `X-Refresh-Token` 헤더 → 쿠키 방식으로 변경 (`/refresh`와 동일)
- 쿠키가 없어도 에러 없이 처리 (optional)

#### 6. Presigned URL 허용 확장

- SVG 파일 확장자 허용 (`.svg`)
- `tables` 디렉토리 허용

#### 7. Bean Validation 추가

- `spring-boot-starter-validation` 의존성 추가
- `@Valid`, `@NotNull`, `@NotEmpty` 등 검증 어노테이션 사용 가능

#### 8. Discord 웹훅 알림 기능

- 가게 생성 시 Discord로 알림 전송
- Spring Event 기반 비동기 처리

**Discord 알림 흐름**:

```
StoreService.createStore()
  → eventPublisher.publishEvent(StoreCreatedEvent)
  → StoreEventListener.handleStoreCreated() [@Async]
  → DiscordWebhookService.sendEmbed()
```

**환경변수**:

```bash
DISCORD_WEBHOOK_URL=https://discord.com/api/webhooks/xxx/yyy
```

**변경 파일**:

- `MenuRepository.java` - `findAllByIds()` 추가
- `MenuRepositoryImpl.java` - 배치 조회 구현
- `OrderService.java` - N+1 수정, 테이블 검증 추가, getOrder 반환타입 변경
- `OrderController.java` - `getOrdersByTableId` 제거
- `OrderApi.java` - 인터페이스 정리
- `AuthController.java` - 로그아웃 쿠키 방식으로 변경
- `S3Service.java` - `.svg`, `tables` 허용
- `build.gradle` (api) - validation 의존성 추가
- `build.gradle` (external) - web 의존성 추가
- `DiscordProperties.java` - 신규
- `DiscordWebhookService.java` - 신규
- `StoreCreatedEvent.java` - 신규
- `StoreEventListener.java` - 신규
- `StoreService.java` - 이벤트 발행 추가
- `OrderpingApiApplication.java` - `@EnableAsync` 추가
- `application.yml` - `discord.webhook-url` 설정 추가
- `OrderRepositoryImplTest.java` - `tableNum` 필드 추가
- `PaymentRepositoryImplTest.java` - `tableNum` 필드 추가

**주요 결정**:

- Discord 알림은 비동기로 처리하여 API 응답 지연 방지
- 이벤트 기반 구조로 향후 이메일/SMS 등 확장 용이
- 로그아웃은 쿠키가 없어도 성공 응답 (쿠키 삭제만 수행)

---

### 2026-02-04

**작업 내용**:

#### 1. 테이블 조회 시 같은 메뉴 수량 합치기

- `/api/tables?storeId=..` 조회 시 동일 메뉴의 수량을 합산하여 반환
- `LinkedHashMap`을 사용하여 메뉴별 집계

**변경 전**:

```json
"orderMenus": [
  { "menuId": 1, "menuName": "소주", "quantity": 2 },
  { "menuId": 1, "menuName": "소주", "quantity": 3 }
]
```

**변경 후**:

```json
"orderMenus": [
  { "menuId": 1, "menuName": "소주", "quantity": 5 }
]
```

#### 2. 종료된 테이블 주문 차단

- `createOrder`, `createServiceOrder`에서 CLOSED 상태 테이블 주문 시 400 에러 반환
- 에러 메시지: "종료된 테이블에는 주문할 수 없습니다."

#### 3. 주문 목록에서 종료된 테이블 제외

- `/api/orders?storeId=..` 조회 시 CLOSED 테이블의 주문 필터링
- 종료된 테이블 주문은 추후 통계 기능에서 집계 예정

#### 4. 주문 API 가격 자동 계산

- 요청에서 `price`, `isService` 필드 제거
- 서버에서 메뉴 DB 조회하여 가격 자동 계산

**주문 생성 요청 (변경 후)**:

```json
POST /api/customer/orders
{
  "tableId": 1,
  "tableNum": 3,
  "storeId": 1,
  "depositorName": "홍길동",
  "couponAmount": 0,
  "menus": [
    { "menuId": 1, "quantity": 2 },
    { "menuId": 2, "quantity": 1 }
  ]
}
```

#### 5. 서비스(무료) 주문 API 분리

- 서비스 주문용 별도 API 추가
- 가격 0원, `isService: true`로 저장

**API**:

```json
POST /api/customer/order
{
  "tableId": 1,
  "tableNum": 3,
  "storeId": 1,
  "menus": [
    { "menuId": 1, "quantity": 1 }
  ]
}
```

**변경 파일**:

- `StoreTableService.java` - 메뉴 수량 합산 로직 추가
- `OrderService.java` - 테이블 상태 검증, 가격 자동 계산, 종료 테이블 필터링
- `OrderCreateRequest.java` - `price`, `isService` 필드 제거
- `ServiceOrderCreateRequest.java` - 신규 (서비스 주문용 DTO)
- `ServiceOrderController.java` - 신규 (서비스 주문 컨트롤러)

**주요 결정**:

- 일반 주문과 서비스 주문 API 분리하여 책임 명확화
- 가격은 클라이언트가 아닌 서버에서 계산 (보안 및 일관성)
- 종료된 테이블 주문은 운영 화면에서 숨기고 통계에서만 사용

---

### 2026-02-10

**작업 내용**:

#### 1. Discord 알림 줄바꿈 수정

- `StoreEventListener`에서 `\\n` → `\n`으로 수정
- Discord 임베드 메시지에서 줄바꿈이 정상 표시되도록 개선

#### 2. 서비스 주문 개선

- 주문자명(`depositorName`)에 `"서비스"` 자동 입력
- 서비스 주문 상태를 `PENDING` → `COMPLETE`로 변경 (즉시 완료 처리)

**변경 파일**:

- `StoreEventListener.java` - 줄바꿈 문자 수정
- `OrderService.java` - 서비스 주문 depositorName, status 변경

**주요 결정**:

- 서비스 주문은 사장님이 직접 넣는 것이므로 별도 확인 없이 바로 완료 처리
- 주문 디스코드 알림은 TPS 고려하여 배치 방식으로 추후 구현 예정

---

### 2026-03-01

**작업 내용**:

#### 1. 주문 시스템 QR 지속성 개선 (storeId + tableId → storeId + tableNum)

- `clearTable` 후 테이블 ID가 바뀌어도 QR 재발급이 불필요하도록 변경
- 고객 주문 API에서 `tableId` 제거, `storeId + tableNum` 조합으로 활성 테이블 조회
- `CustomerOrderController`, `CustomerMenuController` 경로/파라미터 변경

**고객 API 변경**:

```
[변경 전]
POST /api/customer/orders        body: { tableId, storeId, ... }
GET  /api/customer/orders/table/{tableId}
GET  /api/customer/menus/tables/{tableId}

[변경 후]
POST /api/customer/orders        body: { tableNum, storeId, ... }
GET  /api/customer/orders/table?storeId=1&tableNum=3
GET  /api/customer/stores/{storeId}?tableNum=3
```

#### 2. TableResolverService 추가

- 동일 `storeId + tableNum`으로 활성 테이블이 2개 이상일 때 하나를 선택하고 나머지를 CLOSED 처리
- 선택 우선순위: ① 주문이 있는 테이블 → ② 가장 최근 생성(id 최대)
- `CustomerTableService`, `TableQrService`, `OrderService`에서 모두 활용

#### 3. 주문 취소 시 재고 복구

- `OrderService.deleteOrder()` 에서 주문 삭제 전 `menuRepository.increaseStock()` 호출
- `MenuRepository`, `MenuJpaRepository`, `MenuRepositoryImpl`에 `increaseStock` 메서드 추가
- 재고 복구 시 `isSoldOut = false` 자동 리셋

#### 4. StoreTableResponse DTO에서 qrToken, qrUrl 완전 제거

- `qrToken`, `qrUrl` 필드를 null 반환이 아닌 DTO 자체에서 제거
- `StoreTableService`에서 `QrTokenProvider` 의존성 제거

#### 5. 테이블 QR URL 일괄 조회 API 추가

- `GET /api/tables/qr?storeId={storeId}` 엔드포인트 추가
- 활성(non-CLOSED) 테이블 중 `qrImageUrl`이 등록된 것만 `tableNum` 오름차순으로 반환
- 프론트엔드에서 QR PDF 생성에 활용

**응답**:

```json
{
  "storeId": 1,
  "tables": [
    { "tableNum": 1, "qrImageUrl": "https://..." },
    { "tableNum": 2, "qrImageUrl": "https://..." }
  ]
}
```

#### 6. 테스트 코드 추가

- `TableResolverServiceTest` - 7가지 케이스 (단일/복수 활성 테이블, 주문 유무, QR 보존 등)
- `OrderServiceTest` - 일반 주문 및 서비스 주문 생성 9가지 케이스

**변경 파일**:

- `OrderCreateRequest.java` - `tableId` 필드 제거
- `ServiceOrderCreateRequest.java` - `tableId` 필드 제거
- `OrderService.java` - `resolveActiveTable` 사용, `deleteOrder` 재고 복구 추가
- `CustomerOrderController.java` - `/table?storeId=&tableNum=` 방식으로 변경
- `CustomerMenuController.java` - `/api/customer/stores/{storeId}?tableNum=` 경로 변경
- `CustomerMenuApi.java` - 인터페이스 변경
- `CustomerMenuService.java` - `storeId + tableNum` 기반 조회로 변경
- `CustomerTableService.java` - `TableResolverService` 주입
- `TableResolverService.java` - 신규 (활성 테이블 충돌 해결 서비스)
- `StoreTableRepository.java` - `findAllActiveByStoreIdAndTableNum` 추가
- `StoreTableJpaRepository.java` - `findAllByStoreIdAndTableNumAndStatusNot` 추가
- `StoreTableRepositoryImpl.java` - 구현체 추가
- `TableQrService.java` - `TableResolverService` 사용으로 변경
- `MenuRepository.java` - `increaseStock` 추가
- `MenuJpaRepository.java` - `@Modifying @Query`로 재고 복구 쿼리 추가
- `MenuRepositoryImpl.java` - `increaseStock` 구현
- `StoreTableResponse.java` - `qrToken`, `qrUrl` 필드 완전 제거
- `StoreTableService.java` - `QrTokenProvider` 제거, `getQrImageUrls` 추가
- `StoreTableController.java` - `GET /api/tables/qr` 엔드포인트 추가
- `StoreTableApi.java` - Swagger 문서 추가
- `TableQrUrlResponse.java` - 신규 DTO
- `TableQrUrlsResponse.java` - 신규 래퍼 DTO
- `TableResolverServiceTest.java` - 신규
- `OrderServiceTest.java` - 신규
- `StoreTableServiceTest.java` - 기존 테스트 수정 (menuId 중복 버그 수정)

**주요 결정**:

- QR은 `tableNum` 기준으로 지속 → 테이블 교체 후에도 QR 재발급 불필요
- 동시 활성 테이블 충돌은 `TableResolverService`에서 자동 정리
- 재고 복구는 주문 취소(운영자 삭제) 시에만 동작
- QR PDF 생성은 프론트엔드에서 담당, 백엔드는 URL 배열만 제공

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
