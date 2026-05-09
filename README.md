# Aingthon

AI 시대 대학생 개발자 멘토링 커뮤니티. 멘토·멘티로 활동하며 원하는 사람을 직접 찾아가 매칭을 신청하고 상담할 수 있는 서비스.

---

## 기술 스택

- **Backend**: Spring Boot 3.5.14, Java 21
- **Database**: Cloud SQL (PostgreSQL)
- **Storage**: Cloud Storage (GCS)
- **Infra**: GCP Cloud Run
- **인증**: Google OAuth2 + JWT
- **실시간 통신**: WebSocket (STOMP)

---

## 로컬 개발 환경 설정

```bash
# 1. 환경변수 설정
cp .env.example .env

# 2. 로컬 DB 실행 (Docker)
docker-compose up -d

# 3. 서버 실행
./gradlew bootRun
```

로컬 DB는 `docker-compose.yml`의 PostgreSQL 컨테이너를 사용한다 (포트 5433).  
GCP Cloud SQL을 사용할 경우 `.env`의 주석 처리된 Cloud SQL 항목으로 교체한다.

> **주의**: Docker 볼륨을 처음 생성한 이후 `POSTGRES_PASSWORD`를 바꿔도 볼륨의 실제 비밀번호는 바뀌지 않는다.  
> 비밀번호 불일치 시 `docker exec aingthon-db psql -U postgres -c "ALTER USER postgres PASSWORD 'postgres';"` 로 재설정한다.

---

## API 명세

### Auth

#### Google OAuth2 로그인

대학교 이메일(`.ac.kr` / `.edu`)을 사용하는 구글 계정만 가입 가능.

```
GET /oauth2/authorization/google
```

브라우저에서 직접 접속. 구글 로그인 완료 후 아래 URL로 리다이렉트된다.

```
{FRONTEND_URL}/oauth/callback.html?token=<JWT>&university=<대학교명>
```

발급된 JWT에는 `email`(subject)과 `university` claim이 포함된다. `university`는 이메일 도메인을 서버에서 해석한 값으로, 프론트에서 변조할 수 없다.

```json
// JWT payload 예시 (디코딩)
{
  "sub": "student@korea.ac.kr",
  "university": "고려대학교",
  "iat": 1746748800,
  "exp": 1747353600
}
```

**Error**

| Code | HTTP | 설명 |
|------|------|------|
| A003 | 401 | 대학교 이메일이 아님 (.ac.kr / .edu 필요) |

---

#### 인증이 필요한 API 호출

로그인 후 발급받은 토큰을 모든 요청 헤더에 포함한다.

```
Authorization: Bearer <token>
```

---

### 공통 응답 형식

**성공**
```json
{
  "success": true,
  "data": { }
}
```

**실패**
```json
{
  "code": "CH001",
  "message": "채팅방을 찾을 수 없습니다."
}
```

**유효성 검증 실패**
```json
{
  "code": "C001",
  "message": "입력값이 올바르지 않습니다.",
  "errors": [
    { "field": "content", "value": "", "reason": "공백일 수 없습니다" }
  ]
}
```

---

### Match

#### 매칭 신청

매칭 신청과 동시에 두 사용자 간 채팅방이 자동 생성된다.

```
POST /api/matches
Authorization: Bearer <token>
```

**Request Body**
```json
{
  "receiverId": 2,
  "reason": "백엔드 개발 멘토링을 받고 싶습니다.",
  "requirements": "Spring Boot 코드 리뷰",
  "preferredMode": "ONLINE",
  "preferredDate": "2026-06-01"
}
```

| 필드 | Type | 필수 | 설명 |
|------|------|------|------|
| receiverId | Long | ✓ | 상대방 유저 ID |
| reason | String | ✓ | 신청 이유 |
| requirements | String | | 요구사항 (선택) |
| preferredMode | Enum | ✓ | `ONLINE` / `OFFLINE` / `NO_PREFERENCE` |
| preferredDate | LocalDate | | 희망 일정 (선택) |

**Response** `201`
```json
{
  "success": true,
  "data": {
    "id": 1,
    "applicantId": 1,
    "receiverId": 2,
    "reason": "백엔드 개발 멘토링을 받고 싶습니다.",
    "requirements": "Spring Boot 코드 리뷰",
    "preferredMode": "ONLINE",
    "preferredDate": "2026-06-01",
    "status": "PENDING",
    "chatRoomId": 1,
    "createdAt": "2026-05-10T01:57:58"
  }
}
```

**Error**

| Code | HTTP | 설명 |
|------|------|------|
| M002 | 409 | 이미 PENDING 상태의 신청이 있음 |
| M003 | 400 | 자기 자신에게 신청 불가 |

---

#### 매칭 신청 내역 조회

```
GET /api/matches/sent       # 내가 신청한 목록
GET /api/matches/received   # 나에게 온 목록
Authorization: Bearer <token>
```

각 항목의 `status` 필드로 `PENDING` / `APPROVED` / `REJECTED` 상태를 확인한다.

**Response** `200`
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "applicantId": 1,
      "receiverId": 2,
      "status": "PENDING",
      "chatRoomId": 1,
      "createdAt": "2026-05-10T01:57:58"
    }
  ]
}
```

---

#### 매칭 승인 / 거절

받는 사람(receiverId)만 호출 가능. `PENDING` 상태일 때만 유효하다.

```
PATCH /api/matches/{matchId}/approve
PATCH /api/matches/{matchId}/reject
Authorization: Bearer <token>
```

**Response** `200` — 변경된 status가 포함된 Match 객체 반환

**Error**

| Code | HTTP | 설명 |
|------|------|------|
| M001 | 404 | 매칭을 찾을 수 없음 |
| M004 | 400 | PENDING 상태가 아님 |
| M005 | 403 | 매칭 참여자가 아님 |

---

#### 일정 제안

`APPROVED` 상태의 매칭에서만 가능. 이미 일정이 있으면 덮어쓴다.

```
POST /api/matches/{matchId}/schedule
Authorization: Bearer <token>
```

**Request Body**
```json
{
  "scheduledDate": "2026-06-15",
  "scheduledTime": "14:00:00",
  "location": "인하대학교 도서관 3층"
}
```

모든 필드 필수.

**Response** `201`
```json
{
  "success": true,
  "data": {
    "id": 1,
    "matchId": 1,
    "applicantId": 1,
    "receiverId": 2,
    "scheduledDate": "2026-06-15",
    "scheduledTime": "14:00:00",
    "location": "인하대학교 도서관 3층",
    "createdAt": "2026-05-10T01:58:36"
  }
}
```

**Error**

| Code | HTTP | 설명 |
|------|------|------|
| M004 | 400 | APPROVED 상태가 아님 |
| M005 | 403 | 매칭 참여자가 아님 |

---

#### 일정 수정

```
PUT /api/matches/{matchId}/schedule
Authorization: Bearer <token>
```

요청 형식은 일정 제안과 동일. 양쪽 참여자 모두 수정 가능. 일정 시간이 지난 후에는 수정 불가.

**Error**

| Code | HTTP | 설명 |
|------|------|------|
| M006 | 404 | 일정이 없음 |
| M007 | 400 | 이미 지난 일정 |

---

#### 일정 내역 조회

```
GET /api/matches/schedules/upcoming   # 예정된 일정
GET /api/matches/schedules/past       # 과거 일정
Authorization: Bearer <token>
```

현재 로그인한 유저가 참여한 모든 매칭의 일정을 반환한다.

**Response** `200`
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "matchId": 1,
      "applicantId": 1,
      "receiverId": 2,
      "scheduledDate": "2026-06-15",
      "scheduledTime": "14:00:00",
      "location": "인하대학교 도서관 3층",
      "createdAt": "2026-05-10T01:58:36"
    }
  ]
}
```

---

### Chat

#### 채팅방 생성

매칭 신청 시 자동 생성된다. 직접 호출이 필요한 경우에만 사용. 두 사용자 사이에 이미 채팅방이 있으면 기존 방을 반환한다.

```
POST /api/chat/rooms?user1Id={user1Id}&user2Id={user2Id}
```

**Response** `201`
```json
{
  "success": true,
  "data": {
    "id": 1,
    "user1Id": 10,
    "user2Id": 20,
    "createdAt": "2026-05-09T12:00:00"
  }
}
```

---

#### 내 채팅방 목록 조회

```
GET /api/chat/rooms?userId={userId}
```

**Response** `200`
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "user1Id": 10,
      "user2Id": 20,
      "createdAt": "2026-05-09T12:00:00"
    }
  ]
}
```

---

#### 이전 메시지 조회

입장 시 이전 대화 내역을 불러올 때 사용한다. 시간 오름차순 정렬.

```
GET /api/chat/rooms/{roomId}/messages
```

| Path Variable | Type | 설명 |
|---------------|------|------|
| roomId | Long | 채팅방 ID |

**Response** `200`
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "roomId": 1,
      "senderId": 10,
      "content": "안녕하세요!",
      "sentAt": "2026-05-09T12:01:00"
    }
  ]
}
```

**Error**

| Code | HTTP | 설명 |
|------|------|------|
| CH001 | 404 | 채팅방을 찾을 수 없음 |

---

### Chat — WebSocket (STOMP)

#### 연결

```
WS /ws
```

SockJS를 지원한다. 연결 후 STOMP CONNECT 프레임을 전송한다.

---

#### 메시지 수신 구독

채팅방 입장 시 구독한다.

```
SUBSCRIBE /sub/chat/room/{roomId}
```

**수신 payload**
```json
{
  "id": 1,
  "roomId": 1,
  "senderId": 10,
  "content": "안녕하세요!",
  "sentAt": "2026-05-09T12:01:00"
}
```

---

#### 메시지 전송

```
SEND /pub/chat/message
```

**전송 payload**
```json
{
  "roomId": 1,
  "senderId": 10,
  "content": "안녕하세요!"
}
```

| 필드 | Type | 필수 | 설명 |
|------|------|------|------|
| roomId | Long | ✓ | 채팅방 ID |
| senderId | Long | ✓ | 발신자 ID |
| content | String | ✓ | 메시지 내용 (공백 불가) |

**Error**

| Code | HTTP | 설명 |
|------|------|------|
| CH001 | 404 | 채팅방을 찾을 수 없음 |
| CH002 | 403 | 채팅방 참여자가 아님 |
