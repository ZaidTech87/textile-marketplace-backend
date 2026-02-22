package com.textile.marketplace.dto.response;

import com.textile.marketplace.model.ChatRoom;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ChatRoomResponse {
    private Long id;
    private String roomCode;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Long lastMessageSenderId;
    private Integer unreadCount;
    private LocalDateTime createdAt;

    // Related entities
    private Long productId;
    private String productTitle;
    private String productImage;

    private Long buyerId;
    private String buyerName;
    private String buyerImage;

    private Long sellerId;
    private String sellerName;
    private String sellerBusinessName;
    private String sellerImage;

    public static ChatRoomResponse fromEntity(ChatRoom chatRoom) {
        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .roomCode(chatRoom.getRoomCode())
                .lastMessage(chatRoom.getLastMessage())
                .lastMessageTime(chatRoom.getLastMessageTime())
                .lastMessageSenderId(chatRoom.getLastMessageSenderId())
                .createdAt(chatRoom.getCreatedAt())
                .productId(chatRoom.getProduct().getId())
                .productTitle(chatRoom.getProduct().getTitle())
                .productImage(chatRoom.getProduct().getCoverImage())
                .buyerId(chatRoom.getBuyer().getId())
                .buyerName(chatRoom.getBuyer().getName())
                .buyerImage(chatRoom.getBuyer().getProfileImage())
                .sellerId(chatRoom.getSeller().getId())
                .sellerName(chatRoom.getSeller().getName())
                .sellerBusinessName(chatRoom.getSeller().getBusinessName())
                .sellerImage(chatRoom.getSeller().getProfileImage())
                .build();
    }
}