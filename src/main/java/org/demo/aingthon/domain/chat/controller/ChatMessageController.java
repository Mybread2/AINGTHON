package org.demo.aingthon.domain.chat.controller;

import jakarta.validation.Valid;
import org.demo.aingthon.domain.chat.dto.ChatMessageRequest;
import org.demo.aingthon.domain.chat.dto.ChatMessageResponse;
import org.demo.aingthon.domain.chat.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatMessageController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatMessageController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat/message")
    public void sendMessage(@Valid ChatMessageRequest request) {
        ChatMessageResponse response = chatService.saveMessage(request);
        messagingTemplate.convertAndSend("/sub/chat/room/" + response.roomId(), response);
    }
}
