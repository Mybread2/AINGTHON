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
# .env 파일에 DB_URL, DB_USERNAME, DB_PASSWORD 등 입력

# 2. Cloud SQL Auth Proxy 실행 (별도 터미널)
cloud-sql-proxy <INSTANCE_CONNECTION_NAME>

# 3. 서버 실행
./gradlew bootRun
```

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
{FRONTEND_URL}/oauth/callback?token=<JWT>
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

### Chat

#### 채팅방 생성

매칭 신청 시 호출. 두 사용자 사이에 이미 채팅방이 있으면 기존 방을 반환한다.

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
