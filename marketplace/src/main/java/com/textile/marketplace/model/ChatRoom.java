package com.textile.marketplace.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Column(unique = true)
    private String roomCode;

    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Long lastMessageSenderId;

    private Integer buyerUnreadCount = 0;
    private Integer sellerUnreadCount = 0;

    private Boolean isBuyerActive = true;
    private Boolean isSellerActive = true;
    private Boolean isBlocked = false;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        this.roomCode = "ROOM_" + System.currentTimeMillis() + "_" +
                (buyer != null ? buyer.getId() : "0") + "_" +
                (seller != null ? seller.getId() : "0");
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}