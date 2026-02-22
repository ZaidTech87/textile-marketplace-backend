package com.textile.marketplace.dto.response;

import com.textile.marketplace.model.ChatMessage;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ChatMessageResponse {
    private Long id;
    private String message;
    private String messageType;
    private String mediaUrl;
    private Boolean isRead;
    private Boolean isDelivered;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;

    private Long senderId;
    private String senderName;
    private String senderImage;

    private Long receiverId;
    private String receiverName;

    public static ChatMessageResponse fromEntity(ChatMessage message) {
        return ChatMessageResponse.builder()
                .id(message.getId())
                .message(message.getMessage())
                .messageType(message.getMessageType().name())
                .mediaUrl(message.getMediaUrl())
                .isRead(message.getIsRead())
                .isDelivered(message.getIsDelivered())
                .createdAt(message.getCreatedAt())
                .readAt(message.getReadAt())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getName())
                .senderImage(message.getSender().getProfileImage())
                .receiverId(message.getReceiver().getId())
                .receiverName(message.getReceiver().getName())
                .build();
    }
}