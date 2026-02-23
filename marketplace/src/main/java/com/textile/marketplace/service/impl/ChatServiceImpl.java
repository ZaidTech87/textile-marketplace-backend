package com.textile.marketplace.service.impl;

import com.textile.marketplace.dto.response.ChatMessageResponse;
import com.textile.marketplace.dto.response.ChatRoomResponse;
import com.textile.marketplace.model.ChatMessage;
import com.textile.marketplace.model.ChatRoom;
import com.textile.marketplace.model.Product;
import com.textile.marketplace.model.User;
import com.textile.marketplace.repository.ChatMessageRepository;
import com.textile.marketplace.repository.ChatRoomRepository;
import com.textile.marketplace.repository.ProductRepository;
import com.textile.marketplace.repository.UserRepository;
import com.textile.marketplace.service.ChatService;
import com.textile.marketplace.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public ChatRoomResponse createOrGetChatRoom(Long productId, Long buyerId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        User seller = product.getSeller();

        // Check if chat room already exists
        ChatRoom existingRoom = chatRoomRepository
                .findByProductIdAndBuyerIdAndSellerId(productId, buyerId, seller.getId())
                .orElse(null);

        if (existingRoom != null) {
            return ChatRoomResponse.fromEntity(existingRoom);
        }

        // Create new chat room
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setProduct(product);
        chatRoom.setBuyer(buyer);
        chatRoom.setSeller(seller);

        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);
        return ChatRoomResponse.fromEntity(savedRoom);
    }

    @Override
    @Transactional
    public ChatMessageResponse sendMessage(Long roomId, Long senderId, String message, MultipartFile media) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        User receiver = sender.getId().equals(chatRoom.getBuyer().getId()) ?
                chatRoom.getSeller() : chatRoom.getBuyer();

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChatRoom(chatRoom);
        chatMessage.setSender(sender);
        chatMessage.setReceiver(receiver);

        // Handle media upload
        if (media != null && !media.isEmpty()) {
            String mediaUrl = cloudinaryService.uploadImage(media, "chat");
            chatMessage.setMediaUrl(mediaUrl);
            chatMessage.setMessageType(ChatMessage.MessageType.IMAGE);
            chatMessage.setMessage(message != null ? message : "📷 Image");
        } else {
            chatMessage.setMessage(message);
            chatMessage.setMessageType(ChatMessage.MessageType.TEXT);
        }

        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);

        // Update chat room
        chatRoom.setLastMessage(savedMessage.getMessage());
        chatRoom.setLastMessageTime(LocalDateTime.now());
        chatRoom.setLastMessageSenderId(senderId);

        // Update unread count
        if (sender.getId().equals(chatRoom.getBuyer().getId())) {
            chatRoom.setSellerUnreadCount(chatRoom.getSellerUnreadCount() + 1);
        } else {
            chatRoom.setBuyerUnreadCount(chatRoom.getBuyerUnreadCount() + 1);
        }

        chatRoomRepository.save(chatRoom);

        // Send real-time notification via WebSocket
        messagingTemplate.convertAndSendToUser(
                receiver.getMobileNumber(),
                "/queue/messages",
                ChatMessageResponse.fromEntity(savedMessage)
        );

        return ChatMessageResponse.fromEntity(savedMessage);
    }

    @Override
    public Page<ChatMessageResponse> getChatMessages(Long roomId, Long userId, Pageable pageable) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        // Verify user is part of this chat
        if (!chatRoom.getBuyer().getId().equals(userId) &&
                !chatRoom.getSeller().getId().equals(userId)) {
            throw new RuntimeException("You are not authorized to view these messages");
        }

        return chatMessageRepository.findByChatRoomOrderByCreatedAtAsc(chatRoom, pageable)
                .map(ChatMessageResponse::fromEntity);
    }

    @Override
    public List<ChatRoomResponse> getUserChatRooms(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return chatRoomRepository.findAllUserChats(user).stream()
                .map(ChatRoomResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public int markMessagesAsRead(Long roomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        // Reset unread count for this user
        if (chatRoom.getBuyer().getId().equals(userId)) {
            chatRoom.setBuyerUnreadCount(0);
        } else if (chatRoom.getSeller().getId().equals(userId)) {
            chatRoom.setSellerUnreadCount(0);
        } else {
            throw new RuntimeException("User not part of this chat");
        }

        chatRoomRepository.save(chatRoom);

        return chatMessageRepository.markMessagesAsRead(chatRoom, userId);
    }

    @Override
    public long getUnreadCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getUserType() == User.UserType.SELLER) {
            return chatRoomRepository.countUnreadForSeller(user);
        } else {
            return chatRoomRepository.countUnreadForBuyer(user);
        }
    }
}