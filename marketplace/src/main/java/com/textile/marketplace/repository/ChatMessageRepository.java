package com.textile.marketplace.repository;

import com.textile.marketplace.model.ChatMessage;
import com.textile.marketplace.model.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Page<ChatMessage> findByChatRoomOrderByCreatedAtAsc(ChatRoom chatRoom, Pageable pageable);

    List<ChatMessage> findByChatRoomAndIsReadFalseAndReceiverId(ChatRoom chatRoom, Long receiverId);

    @Modifying
    @Transactional
    @Query("UPDATE ChatMessage cm SET cm.isRead = true, cm.readAt = CURRENT_TIMESTAMP " +
            "WHERE cm.chatRoom = :chatRoom AND cm.receiver.id = :receiverId AND cm.isRead = false")
    int markMessagesAsRead(@Param("chatRoom") ChatRoom chatRoom, @Param("receiverId") Long receiverId);

    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.receiver.id = :userId AND cm.isRead = false")
    long countUnreadMessages(@Param("userId") Long userId);
}