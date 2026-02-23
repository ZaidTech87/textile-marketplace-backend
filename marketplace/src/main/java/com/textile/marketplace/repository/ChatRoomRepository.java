package com.textile.marketplace.repository;

import com.textile.marketplace.model.ChatRoom;
import com.textile.marketplace.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByProductIdAndBuyerIdAndSellerId(Long productId, Long buyerId, Long sellerId);

    Optional<ChatRoom> findByRoomCode(String roomCode);

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.seller = :seller ORDER BY cr.lastMessageTime DESC")
    List<ChatRoom> findBySeller(@Param("seller") User seller);

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.buyer = :buyer ORDER BY cr.lastMessageTime DESC")
    List<ChatRoom> findByBuyer(@Param("buyer") User buyer);

    @Query("SELECT cr FROM ChatRoom cr WHERE (cr.seller = :user OR cr.buyer = :user) ORDER BY cr.lastMessageTime DESC")
    List<ChatRoom> findAllUserChats(@Param("user") User user);

    @Query("SELECT COUNT(cr) FROM ChatRoom cr WHERE cr.seller = :seller AND cr.sellerUnreadCount > 0")
    long countUnreadForSeller(@Param("seller") User seller);

    @Query("SELECT COUNT(cr) FROM ChatRoom cr WHERE cr.buyer = :buyer AND cr.buyerUnreadCount > 0")
    long countUnreadForBuyer(@Param("buyer") User buyer);
}