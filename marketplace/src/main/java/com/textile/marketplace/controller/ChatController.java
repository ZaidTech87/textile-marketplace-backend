package com.textile.marketplace.controller;

import com.textile.marketplace.dto.response.ChatMessageResponse;
import com.textile.marketplace.dto.response.ChatRoomResponse;
import com.textile.marketplace.security.JwtUtil;
import com.textile.marketplace.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/room/{productId}")
    public ResponseEntity<ChatRoomResponse> createOrGetChatRoom(
            @PathVariable Long productId,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token.substring(7));
        ChatRoomResponse response = chatService.createOrGetChatRoom(productId, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomResponse>> getUserChatRooms(
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token.substring(7));
        List<ChatRoomResponse> rooms = chatService.getUserChatRooms(userId);
        return ResponseEntity.ok(rooms);
    }

    @PostMapping("/{roomId}/send")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @PathVariable Long roomId,
            @RequestParam(required = false) String message,
            @RequestParam(required = false) MultipartFile media,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token.substring(7));
        ChatMessageResponse response = chatService.sendMessage(roomId, userId, message, media);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{roomId}/messages")
    public ResponseEntity<Page<ChatMessageResponse>> getMessages(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token.substring(7));
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Page<ChatMessageResponse> messages = chatService.getChatMessages(roomId, userId, pageable);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/{roomId}/read")
    public ResponseEntity<Integer> markMessagesAsRead(
            @PathVariable Long roomId,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token.substring(7));
        int count = chatService.markMessagesAsRead(roomId, userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token.substring(7));
        long count = chatService.getUnreadCount(userId);
        return ResponseEntity.ok(count);
    }
}