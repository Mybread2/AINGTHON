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
- **Database**: Cloud SQL (PostgreSQL) — 로컬 개발 시 Cloud SQL Auth Proxy로 연결
- **File Storage**: Cloud Storage (GCS) — 프로필 사진 등 유저 업로드 파일, Signed URL로 접근 제어
- **Secret 관리**: Secret Manager — DB 비밀번호, OAuth 클라이언트 시크릿 등 민감 정보 관리

**패키지 구조 (도메인 기반):**
```
org.demo.aingthon/
  auth/           # 회원가입, 로그인, 학교 이메일 OAuth2 인증
  profile/        # 프로필 등록·수정·조회, 분야·학교 필터링, 리뷰 작성·조회, 활동 이력 노출
  match/          # 매칭 신청(Pending→Confirmed→Completed), 신청폼, 최종 일정 확정
  chat/           # 채팅방 생성·메시지
  global/
    config/       # JpaAuditingConfig 등 설정 빈
    entity/       # BaseEntity (createdAt, updatedAt)
    exception/    # 예외처리 시스템
    response/     # 공통 응답 래퍼
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

### JPA Auditing — `BaseEntity`
모든 엔티티는 `BaseEntity`를 상속한다. `createdAt` / `updatedAt`이 자동으로 채워진다.
```java
@Entity
public class User extends BaseEntity { ... }
```

애플리케이션 설정은 `src/main/resources/application.yaml`. 환경별 오버라이드는 `application-dev.yaml` / `application-prod.yaml` + `spring.profiles.active`.

환경변수는 `.env` 파일로 관리한다 (`spring-dotenv` 라이브러리가 자동 로드). `.env.example`을 복사해 `.env`를 만들고 값을 채운다. `.env`는 `.gitignore`에 포함되어야 한다.

로컬 개발 시 Cloud SQL 연결은 Cloud SQL Auth Proxy를 사용한다:
```bash
cloud-sql-proxy <INSTANCE_CONNECTION_NAME>
# 이후 DB_URL=jdbc:postgresql://localhost:5432/aingthon 으로 접속
```
