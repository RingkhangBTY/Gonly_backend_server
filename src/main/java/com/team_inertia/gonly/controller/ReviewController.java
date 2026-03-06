package com.team_inertia.gonly.controller;

import com.team_inertia.gonly.dto.*;
import com.team_inertia.gonly.model.User;
import com.team_inertia.gonly.service.AuthService;
import com.team_inertia.gonly.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gems/{gemId}/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private AuthService authService;

    // GET /api/gems/{gemId}/reviews — PUBLIC
    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getReviews(@PathVariable Long gemId) {
        return ResponseEntity.ok(reviewService.getReviewsByGemId(gemId));
    }

    // POST /api/gems/{gemId}/reviews — PROTECTED
    @PostMapping
    public ResponseEntity<?> addReview(
            @PathVariable Long gemId,
            @RequestBody ReviewRequest request,
            Authentication authentication) {
        try {
            User user = authService.getUserByEmail(authentication.getName());
            ReviewResponse response = reviewService.addReview(gemId, request, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // DELETE /api/gems/{gemId}/reviews/{reviewId} — PROTECTED
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(
            @PathVariable Long gemId,
            @PathVariable Long reviewId,
            Authentication authentication) {
        try {
            User user = authService.getUserByEmail(authentication.getName());
            reviewService.deleteReview(reviewId, user);
            return ResponseEntity.ok(new ApiResponse(true, "Review deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
}