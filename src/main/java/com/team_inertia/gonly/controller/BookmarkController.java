package com.team_inertia.gonly.controller;

import com.team_inertia.gonly.dto.*;
import com.team_inertia.gonly.model.User;
import com.team_inertia.gonly.service.AuthService;
import com.team_inertia.gonly.service.BookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {

    @Autowired
    private BookmarkService bookmarkService;

    @Autowired
    private AuthService authService;

    // POST /api/bookmarks/{gemId} — Toggle bookmark (PROTECTED)
    @PostMapping("/{gemId}")
    public ResponseEntity<?> toggleBookmark(
            @PathVariable Long gemId,
            Authentication authentication) {
        try {
            User user = authService.getUserByEmail(authentication.getName());
            boolean isBookmarked = bookmarkService.toggleBookmark(gemId, user);
            String message = isBookmarked ? "Gem bookmarked" : "Bookmark removed";
            return ResponseEntity.ok(new ApiResponse(true, message));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // GET /api/bookmarks — My bookmarks (PROTECTED)
    @GetMapping
    public ResponseEntity<List<GemResponse>> getMyBookmarks(Authentication authentication) {
        User user = authService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(bookmarkService.getMyBookmarks(user.getId()));
    }

    // GET /api/bookmarks/check/{gemId} — Check if bookmarked (PROTECTED)
    @GetMapping("/check/{gemId}")
    public ResponseEntity<?> isBookmarked(
            @PathVariable Long gemId,
            Authentication authentication) {
        User user = authService.getUserByEmail(authentication.getName());
        boolean result = bookmarkService.isBookmarked(gemId, user.getId());
        return ResponseEntity.ok(new ApiResponse(result, result ? "Bookmarked" : "Not bookmarked"));
    }
}