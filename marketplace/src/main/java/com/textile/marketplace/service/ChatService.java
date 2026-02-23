\package com.textile.marketplace.service;

import com.textile.marketplace.dto.response.ChatMessageResponse;
import com.textile.marketplace.dto.response.ChatRoomResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ChatService {

    ChatRoomResponse createOrGetChatRoom(Long productId, Long buyerId);

    ChatMessageResponse sendMessage(Long roomId, Long senderId, String message, MultipartFile media);

    Page<ChatMessageResponse> getChatMessages(Long roomId, Long userId, Pageable pageable);

    List<ChatRoomResponse> getUserChatRooms(Long userId);

    int markMessagesAsRead(Long roomId, Long userId);

    long getUnreadCount(Long userId);
}