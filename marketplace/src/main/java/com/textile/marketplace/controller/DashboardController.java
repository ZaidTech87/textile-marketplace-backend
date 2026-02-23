package com.textile.marketplace.controller;

import com.textile.marketplace.dto.response.ProductResponse;
import com.textile.marketplace.dto.response.UserResponse;
import com.textile.marketplace.security.JwtUtil;
import com.textile.marketplace.service.ChatService;
import com.textile.marketplace.service.ProductService;
import com.textile.marketplace.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/seller/stats")
    public ResponseEntity<Map<String, Object>> getSellerStats(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token.substring(7));
        UserResponse user = userService.getUserById(userId);

        Pageable pageable = PageRequest.of(0, 100);
        Page<ProductResponse> products = productService.getSellerProducts(userId, pageable);

        long totalViews = products.getContent().stream()
                .mapToLong(ProductResponse::getViews)
                .sum();

        long totalChats = products.getContent().stream()
                .mapToLong(ProductResponse::getChatCount)
                .sum();

        long unreadCount = chatService.getUnreadCount(userId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalProducts", products.getTotalElements());
        stats.put("totalViews", totalViews);
        stats.put("totalChats", totalChats);
        stats.put("unreadMessages", unreadCount);
        stats.put("rating", user.getRating());
        stats.put("totalReviews", user.getTotalReviews());

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/buyer/stats")
    public ResponseEntity<Map<String, Object>> getBuyerStats(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token.substring(7));

        long unreadCount = chatService.getUnreadCount(userId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("unreadMessages", unreadCount);

        return ResponseEntity.ok(stats);
    }
}