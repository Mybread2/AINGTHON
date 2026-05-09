package org.demo.aingthon.domain.chat.controller;

import org.demo.aingthon.domain.chat.dto.ChatMessageResponse;
import org.demo.aingthon.domain.chat.dto.ChatRoomResponse;
import org.demo.aingthon.domain.chat.service.ChatService;
import org.demo.aingthon.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/rooms")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> createRoom(
            @RequestParam Long user1Id,
            @RequestParam Long user2Id
    ) {
        return ResponseEntity.status(201).body(ApiResponse.created(chatService.createRoom(user1Id, user2Id)));
    }

    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<List<ChatRoomResponse>>> getRooms(@RequestParam Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(chatService.getRooms(userId)));
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> getMessages(@PathVariable Long roomId) {
        return ResponseEntity.ok(ApiResponse.ok(chatService.getMessages(roomId)));
    }
}
