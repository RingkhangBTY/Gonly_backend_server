package com.team_inertia.gonly.controller;

import com.team_inertia.gonly.dto.*;
import com.team_inertia.gonly.model.User;
import com.team_inertia.gonly.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    @Autowired
    private AuthService authService;

    // POST /api/auth/register — PUBLIC
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User user = authService.register(request);
            log.info("User requested for register");
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "User registered successfully with id: " + user.getId()));
        } catch (RuntimeException e) {
            log.error("Unable to register user : {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // POST /api/auth/login — PUBLIC
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            log.info("User {} successfully longed in.",response.getFullName());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.warn("Failed to authenticate user : {}",e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Invalid email or password"));
        }
    }

    // ==================== GET MY PROFILE ====================
    // GET /api/auth/profile
    // PROTECTED — JWT required
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        try {
            User user = authService.getUserByEmail(authentication.getName());

            log.info("Profile fetched for user: {}", user.getEmail());

            // Build profile response (don't expose password!)
            Map<String, Object> profile = new HashMap<>();
            profile.put("id", user.getId());
            profile.put("email", user.getEmail());
            profile.put("fullName", user.getFullName());
            profile.put("homeState", user.getHomeState());
            profile.put("bio", user.getBio());
            profile.put("role", user.getRole().name());
            profile.put("createdAt", user.getCreatedAt());

            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
}