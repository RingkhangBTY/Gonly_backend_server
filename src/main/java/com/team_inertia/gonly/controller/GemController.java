package com.team_inertia.gonly.controller;

import com.team_inertia.gonly.dto.*;
import com.team_inertia.gonly.model.GemImage;
import com.team_inertia.gonly.model.User;
import com.team_inertia.gonly.service.AuthService;
import com.team_inertia.gonly.service.GemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/gems")
public class GemController {

    @Autowired
    private GemService gemService;

    @Autowired
    private AuthService authService;

    // ==================== PUBLIC ENDPOINTS ====================

    // GET /api/gems — Get all approved gems
    @GetMapping
    public ResponseEntity<List<GemResponse>> getAllGems() {
        return ResponseEntity.ok(gemService.getAllApprovedGems());
    }

    // GET /api/gems/{id} — Get gem by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getGemById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(gemService.getGemById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // ==================== ENHANCED SEARCH ENDPOINTS ====================

    /**
     * Basic search by name only
     * GET /api/gems/search?q=Mawlynnong
     */
    @GetMapping("/search")
    public ResponseEntity<List<GemResponse>> searchGems(@RequestParam String q) {
        return ResponseEntity.ok(gemService.advancedSearch(q));
    }

    /**
     * Advanced search across all fields with relevance ranking
     * GET /api/gems/search/advanced?q=clean village meghalaya
     */
    @GetMapping("/search/advanced")
    public ResponseEntity<List<GemResponse>> advancedSearch(@RequestParam String q) {
        return ResponseEntity.ok(gemService.advancedSearch(q));
    }

    /**
     * Multi-keyword search (matches ANY keyword)
     * GET /api/gems/search/multi?q=waterfall trekking nature
     */
    @GetMapping("/search/multi")
    public ResponseEntity<List<GemResponse>> multiKeywordSearch(@RequestParam String q) {
        return ResponseEntity.ok(gemService.multiKeywordSearch(q));
    }

    /**
     * Fuzzy search (handles typos)
     * GET /api/gems/search/fuzzy?q=mawlynong (typo: should be mawlynnong)
     */
    @GetMapping("/search/fuzzy")
    public ResponseEntity<List<GemResponse>> fuzzySearch(@RequestParam String q) {
        return ResponseEntity.ok(gemService.fuzzySearch(q));
    }

    // ==================== FILTER ENDPOINTS ====================

    // GET /api/gems/category?type=NATURE
    @GetMapping("/category")
    public ResponseEntity<List<GemResponse>> filterByCategory(@RequestParam String type) {
        try {
            return ResponseEntity.ok(gemService.filterByCategory(type));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(List.of());
        }
    }

    // GET /api/gems/state?name=Meghalaya
    @GetMapping("/state")
    public ResponseEntity<List<GemResponse>> filterByState(@RequestParam String name) {
        return ResponseEntity.ok(gemService.filterByState(name));
    }

    // GET /api/gems/nearby?lat=25.57&lng=91.88&radiusKm=50
    @GetMapping("/nearby")
    public ResponseEntity<List<GemResponse>> getNearbyGems(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "50") double radiusKm) {
        return ResponseEntity.ok(gemService.getNearbyGems(lat, lng, radiusKm));
    }

    // GET /api/gems/images/{imageId}
    @GetMapping("/images/{imageId}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long imageId) {
        try {
            GemImage image = gemService.getImageById(imageId);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, image.getImageType())
                    .body(image.getImageData());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==================== PROTECTED ENDPOINTS ====================

    // POST /api/gems
    @PostMapping
    public ResponseEntity<?> createGem(
            @RequestBody GemRequest request,
            Authentication authentication) {
        try {
            User user = authService.getUserByEmail(authentication.getName());
            GemResponse response = gemService.createGem(request, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // POST /api/gems/{id}/images
    @PostMapping("/{id}/images")
    public ResponseEntity<?> uploadImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile file,
            Authentication authentication) {
        try {
            User user = authService.getUserByEmail(authentication.getName());
            gemService.addImageToGem(id, file, user);
            return ResponseEntity.ok(new ApiResponse(true, "Image uploaded successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // GET /api/gems/my
    @GetMapping("/my")
    public ResponseEntity<List<GemResponse>> getMyGems(Authentication authentication) {
        User user = authService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(gemService.getMySubmissions(user.getId()));
    }

    // DELETE /api/gems/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGem(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            User user = authService.getUserByEmail(authentication.getName());
            gemService.deleteGem(id, user);
            return ResponseEntity.ok(new ApiResponse(true, "Gem deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
}