package com.textile.marketplace.controller;

import com.textile.marketplace.dto.response.UserResponse;
import com.textile.marketplace.model.User;
import com.textile.marketplace.security.JwtUtil;
import com.textile.marketplace.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getMyProfile(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token.substring(7));
        UserResponse user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(
            @RequestBody User userDetails,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token.substring(7));
        UserResponse user = userService.updateProfile(userId, userDetails);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/profile/image")
    public ResponseEntity<UserResponse> uploadProfileImage(
            @RequestParam("image") MultipartFile image,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token.substring(7));
        UserResponse user = userService.uploadProfileImage(userId, image);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/local-sellers")
    public ResponseEntity<List<UserResponse>> getLocalSellers(
            @RequestParam String city,
            @RequestParam(required = false) String area) {
        List<UserResponse> sellers = userService.getLocalSellers(city, area);
        return ResponseEntity.ok(sellers);
    }

    @GetMapping("/top-rated")
    public ResponseEntity<List<UserResponse>> getTopRatedSellers() {
        List<UserResponse> sellers = userService.getTopRatedSellers();
        return ResponseEntity.ok(sellers);
    }
}