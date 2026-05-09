package org.demo.aingthon.domain.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.demo.aingthon.domain.auth.entity.User;
import org.demo.aingthon.domain.chat.dto.ChatMessageResponse;
import org.demo.aingthon.domain.chat.dto.ChatRoomResponse;
import org.demo.aingthon.domain.chat.service.ChatService;
import org.demo.aingthon.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Chat", description = "채팅 API (메시지 전송은 WebSocket STOMP /pub/chat/message 사용)")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @Operation(summary = "채팅방 생성", description = "user1Id와 user2Id 사이의 채팅방을 생성합니다. 이미 존재하면 기존 채팅방을 반환합니다.")
    @PostMapping("/rooms")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> createRoom(
            @RequestParam Long user1Id,
            @RequestParam Long user2Id
    ) {
        return ResponseEntity.status(201).body(ApiResponse.created(chatService.createRoom(user1Id, user2Id)));
    }

    @Operation(summary = "채팅방 목록 조회")
    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<List<ChatRoomResponse>>> getRooms(@RequestParam Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(chatService.getRooms(userId)));
    }

    @Operation(summary = "채팅 메시지 목록 조회", description = "해당 채팅방의 전체 메시지를 시간 오름차순으로 반환합니다.")
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> getMessages(@PathVariable Long roomId) {
        return ResponseEntity.ok(ApiResponse.ok(chatService.getMessages(roomId)));
    }

    @Operation(summary = "채팅방 파일 업로드", description = "파일을 GCS에 업로드하고 7일짜리 Signed URL을 반환합니다. multipart/form-data (key: file)")
    @PostMapping("/rooms/{roomId}/files")
    public ResponseEntity<ApiResponse<String>> uploadFile(
            @AuthenticationPrincipal User user,
            @PathVariable Long roomId,
            @RequestParam("file") MultipartFile file) {
        String signedUrl = chatService.uploadFile(user, roomId, file);
        return ResponseEntity.ok(ApiResponse.ok(signedUrl));
    }
}
