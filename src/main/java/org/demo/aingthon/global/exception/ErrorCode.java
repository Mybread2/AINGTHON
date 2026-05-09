package org.demo.aingthon.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "입력값이 올바르지 않습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C002", "서버 내부 오류가 발생했습니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "C003", "요청한 리소스를 찾을 수 없습니다."),

    // Auth
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A001", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "A002", "접근 권한이 없습니다."),
    NOT_UNIVERSITY_EMAIL(HttpStatus.BAD_REQUEST, "A003", "대학교 이메일로만 가입할 수 있습니다."),
    ALREADY_REGISTERED(HttpStatus.CONFLICT, "A004", "이미 가입된 이메일입니다."),

    // Profile
    PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "프로필을 찾을 수 없습니다."),
    PROFILE_ALREADY_EXISTS(HttpStatus.CONFLICT, "P002", "프로필이 이미 존재합니다."),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "P003", "리뷰를 찾을 수 없습니다."),
    CANNOT_REVIEW_SELF(HttpStatus.BAD_REQUEST, "P004", "자기 자신에게 리뷰를 작성할 수 없습니다."),

    // Match
    MATCH_NOT_FOUND(HttpStatus.NOT_FOUND, "M001", "매칭 신청을 찾을 수 없습니다."),
    MATCH_ALREADY_PENDING(HttpStatus.CONFLICT, "M002", "이미 신청 중인 매칭이 있습니다."),
    CANNOT_MATCH_SELF(HttpStatus.BAD_REQUEST, "M003", "자기 자신에게 매칭을 신청할 수 없습니다."),
    INVALID_MATCH_STATUS(HttpStatus.BAD_REQUEST, "M004", "현재 상태에서 허용되지 않는 작업입니다."),

    // Chat
    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "CH001", "채팅방을 찾을 수 없습니다."),
    CHAT_ROOM_ACCESS_DENIED(HttpStatus.FORBIDDEN, "CH002", "채팅방에 접근할 권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    public HttpStatus getHttpStatus() { return httpStatus; }
    public String getCode() { return code; }
    public String getMessage() { return message; }
}
