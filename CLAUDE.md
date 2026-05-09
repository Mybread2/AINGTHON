# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**AI 시대 대학생 개발자 멘토링 커뮤니티** — AI로 인한 불안감을 느끼는 대학생 개발자들을 연결해주는 서비스. 사용자는 멘토·멘티 역할을 모두 수행할 수 있으며, 직접 원하는 사람을 찾아가 매칭을 신청한다.

### 핵심 기능

1. **내 정보 작성** — 프로필 등록 (분야, 학교, 경험 등)
2. **필요한 사람 서칭** — 필터 기반 사용자 탐색
3. **매칭 신청 및 상담** — 신청 → 채팅 → 일정 확정 → 리뷰

### 사용자 플로우

```
회원가입 (대학생 인증 필수)
  └─ 프로필 등록
       └─ 사용자 탐색 (필터링)
            └─ 상대 프로필 조회
                 └─ 매칭 신청 (신청폼 작성) → Pending 상태
                      └─ 채팅방에서 일정 조율
                           └─ 최종 신청 확정 (일시·장소·내용)
                                └─ 상담 완료 후 상호 리뷰 작성
                                     └─ 원하는 활동을 프로필에 노출
```

### 프로필 항목

- 분야: `백엔드 / 프론트엔드 / UI·UX / AI·에이전트 / 기획·PM / 임베디드 / Android / iOS / 게임 / 그래픽스`
- 소속 대학교 (학교 이메일 OAuth2 인증 검토 중)
- 경험·소개 등 자유 기재

### 탐색 필터

- 같은 학교 / 다른 학교
- 분야 (위 목록)

### 매칭 신청폼 항목

- 하고 싶은 것
- 신청 이유
- 가능한 시간
- 가능한 지역 (온라인 / 오프라인)

---

## Commands

```bash
# Build
./gradlew build

# Run tests
./gradlew test

# Run a single test class
./gradlew test --tests "org.demo.aingthon.SomeTestClass"

# Start dev server
./gradlew bootRun

# Clean build artifacts
./gradlew clean
```

> Java 17+ 필요 (toolchain 타겟: Java 21). Gradle 8.14.4는 wrapper가 자동으로 다운로드.

---

## Architecture

**3계층 아키텍처** — Controller → Service → Repository

Spring Boot 3.5.14 REST API, Java 21, Gradle. 루트 패키지: `org.demo.aingthon`.

**기술 스택:**
- `spring-boot-starter-web` — REST 컨트롤러
- `spring-boot-starter-data-jpa` + PostgreSQL — 데이터 접근 계층 (DB: Supabase)
- `spring-boot-starter-security` + `spring-boot-starter-oauth2-client` — 인증 (학교 이메일 OAuth2 검토 중)
- `spring-boot-starter-validation` — 요청 유효성 검사

**인프라 (GCP 중심):**
- **Database**: Cloud SQL (PostgreSQL) — 로컬 개발 시 Docker Compose PostgreSQL (포트 5433) 사용
- **File Storage**: Cloud Storage (GCS) — 프로필 사진 등 유저 업로드 파일, Signed URL로 접근 제어
- **Secret 관리**: Secret Manager — DB 비밀번호, OAuth 클라이언트 시크릿 등 민감 정보 관리

**패키지 구조 (도메인 기반):**
```
org.demo.aingthon/
  domain/
    auth/           # Google OAuth2 로그인, 대학교 이메일 검증, JWT 발급
    profile/        # 프로필 등록·수정·조회, 분야·학교 필터링, 리뷰 작성·조회, 활동 이력 노출
    match/          # 매칭 신청(Pending→Confirmed→Completed), 신청폼, 최종 일정 확정
    chat/           # 채팅방 생성·메시지 (REST + WebSocket STOMP)
  global/
    config/         # JpaAuditingConfig, WebSocketConfig, SecurityConfig
    entity/         # BaseEntity (createdAt, updatedAt)
    exception/      # 예외처리 시스템
    jwt/            # JwtTokenProvider, JwtAuthenticationFilter
    response/       # 공통 응답 래퍼
```

각 도메인 패키지 내부는 3계층으로 구성: `controller` / `service` / `repository` + `entity` + `dto`

---

## Global Conventions

### 공통 응답 — `ApiResponse<T>`
모든 컨트롤러 응답은 `ApiResponse`로 감싼다.
```java
return ResponseEntity.ok(ApiResponse.ok(data));
return ResponseEntity.status(201).body(ApiResponse.created(data));
return ResponseEntity.ok(ApiResponse.message("처리 완료"));
```

### 예외처리
비즈니스 예외는 `BusinessException`에 `ErrorCode`를 담아 던진다. `GlobalExceptionHandler`가 자동으로 `ErrorResponse`로 변환한다.
```java
throw new BusinessException(ErrorCode.PROFILE_NOT_FOUND);
throw new BusinessException(ErrorCode.MATCH_NOT_FOUND, "매칭 ID: " + id);
```
새 에러 코드는 `ErrorCode.java`에 도메인 구분 주석 아래 추가한다. 코드 prefix 규칙: `C`(공통) / `A`(Auth) / `P`(Profile) / `M`(Match) / `CH`(Chat).

`@Valid` 검증 실패는 핸들러가 필드별 오류 목록(`FieldError`)을 자동으로 응답에 포함한다.

### 인증 — Google OAuth2 + JWT

**로그인 흐름:**
```
GET /oauth2/authorization/google  → 구글 로그인
                                  → CustomOAuth2UserService (이메일 도메인 검증)
                                  → .ac.kr / .edu 아니면 A003 에러
                                  → UniversityExtractor로 이메일 도메인 → 대학교 이름 변환
                                  → User 저장 (최초 로그인 시, university 포함)
                                  → OAuth2SuccessHandler → JWT 발급 후 프론트 리다이렉트
                                  → {FRONTEND_URL}/oauth/callback.html?token=<JWT>&university=<대학교명>
```

**JWT payload:**  `email` (subject) + `university` claim 포함. 서명된 토큰이므로 프론트에서 변조 불가.

**JWT 사용:**  모든 보호된 API에 헤더 추가
```
Authorization: Bearer <token>
```
`JwtAuthenticationFilter`가 토큰을 검증하고 `SecurityContext`에 유저를 설정한다.

**공개 엔드포인트 (인증 불필요):** `/oauth2/**`, `/login/**`, `/ws/**`, `/oauth/**`, `/`, `/index.html`, `/chat-test.html`

**JWT 설정** (`.env`):
- `JWT_SECRET` — 32자 이상 비밀키
- `FRONTEND_URL` — OAuth2 로그인 완료 후 리다이렉트할 프론트 URL (예: `http://localhost:3000`)
- `jwt.expiration-ms` — 만료 시간 (기본 7일, `application.yml`에서 조정)

**대학교 이름 추출 — `UniversityExtractor`:**  
`domain/auth/util/UniversityExtractor.java`에서 이메일 도메인 → 대학교 이름을 서버 사이드에서 결정한다. 서울·경기·인천 지역 65개 대학 매핑 포함. 미등록 도메인은 도메인 문자열 그대로 반환. 새 대학교 추가 시 이 파일의 `DOMAIN_MAP`에 항목을 추가한다.

### Match 도메인

**상태 흐름:** `PENDING` → `APPROVED` / `REJECTED`  
승인·거절은 `receiverId` 본인만 가능. `PENDING`이 아닌 상태에서 호출 시 M004.

**채팅방 자동 생성:** `POST /api/matches` 호출 시 `ChatService.createRoom()`을 내부적으로 호출하여 채팅방을 함께 생성한다. 응답에 `chatRoomId`가 포함된다.

**일정 관리:**
- `APPROVED` 상태 매칭에서만 일정 제안 가능
- 일정은 매칭당 하나만 유지 (재제안 시 덮어씀)
- 양쪽 참여자 모두 일정 수정 가능
- `Schedule.isPast()` 기준으로 수정 제한 — `LocalDateTime.of(scheduledDate, scheduledTime).isBefore(LocalDateTime.now())`

**에러 코드:**

| Code | 설명 |
|------|------|
| M001 | 매칭 없음 |
| M002 | 이미 PENDING 신청 존재 |
| M003 | 자기 자신에게 신청 |
| M004 | 현재 상태에서 허용되지 않는 작업 |
| M005 | 매칭 참여자가 아님 |
| M006 | 일정 없음 |
| M007 | 이미 지난 일정 수정 불가 |

---

### WebSocket (채팅)
STOMP 프로토콜 사용. 엔드포인트: `/ws` (SockJS 지원).

| 방향 | prefix | 예시 |
|------|--------|------|
| 클라이언트 → 서버 발행 | `/pub` | `/pub/chat/message` |
| 서버 → 클라이언트 구독 | `/sub` | `/sub/chat/room/{roomId}` |

메시지 전송 payload: `{ roomId, senderId, content }`
채팅방 생성·목록·이전 메시지 조회는 REST API 사용 (`/api/chat`).
참여자가 아닌 senderId로 메시지 전송 시 `CH002` 예외.

### JPA Auditing — `BaseEntity`
모든 엔티티는 `BaseEntity`를 상속한다. `createdAt` / `updatedAt`이 자동으로 채워진다.
```java
@Entity
public class User extends BaseEntity { ... }
```

애플리케이션 설정은 `src/main/resources/application.yml`. `ddl-auto: update`로 서버 재시작 시 스키마가 유지된다.

환경변수는 `.env` 파일로 관리한다 (`spring-dotenv` 라이브러리가 자동 로드). `.env.example`을 복사해 `.env`를 만들고 값을 채운다. `.env`는 `.gitignore`에 포함되어야 한다.

로컬 개발 DB는 Docker Compose로 실행한다:
```bash
docker-compose up -d
# DB_URL=jdbc:postgresql://localhost:5433/aingthon
```

> Docker 볼륨은 최초 생성 시 비밀번호가 고정된다. `POSTGRES_PASSWORD`를 변경해도 볼륨의 실제 비밀번호는 바뀌지 않으므로, 비밀번호 불일치 시 `ALTER USER postgres PASSWORD '...'`로 직접 재설정한다.

### 테스트 UI
`src/main/resources/static/`에 브라우저용 테스트 페이지가 있다.

| 경로 | 설명 |
|------|------|
| `/index.html` | 로그인, 매칭 신청/승인, 일정 관리 |
| `/oauth/callback.html` | OAuth2 콜백 처리 (token → localStorage) |
| `/chat-test.html` | WebSocket STOMP 채팅 테스트 |
