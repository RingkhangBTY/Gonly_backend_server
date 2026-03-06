package com.team_inertia.gonly.controller;

import com.team_inertia.gonly.dto.*;
import com.team_inertia.gonly.model.GemImage;
import com.team_inertia.gonly.model.User;
import com.team_inertia.gonly.service.AuthService;
import com.team_inertia.gonly.service.GemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gems")
@Slf4j
public class GemController {

    private final GemService gemService;
    private final AuthService authService;

    public GemController(GemService gemService, AuthService authService) {
        this.gemService = gemService;
        this.authService = authService;
    }

    // ==================== PUBLIC ENDPOINTS ====================

    // GET /api/gems — Get all approved gems
    @GetMapping
    public ResponseEntity<List<GemResponse>> getAllGems() {
        log.info("Requested for all available gems");
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

    // ==================== SINGLE ENHANCED SEARCH ENDPOINTS ====================
    @GetMapping("/search")
    public ResponseEntity<List<GemResponse>> searchGems(@RequestParam String q) {
        return ResponseEntity.ok(gemService.smartSearch(q));
    }

    // ==================== FILTER ENDPOINTS ====================
    @GetMapping("/category")
    public ResponseEntity<List<GemResponse>> filterByCategory(@RequestParam String type) {
        try {
            return ResponseEntity.ok(gemService.filterByCategory(type));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(List.of());
        }
    }

    @GetMapping("/state")
    public ResponseEntity<List<GemResponse>> filterByState(@RequestParam String name) {
        return ResponseEntity.ok(gemService.filterByState(name));
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<GemResponse>> getNearbyGems(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "50") double radiusKm) {
        return ResponseEntity.ok(gemService.getNearbyGems(lat, lng, radiusKm));
    }

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

    // GET /api/gems/{gemId}/images/all
    // PUBLIC — Returns array of base64 encoded images
    @GetMapping("/{gemId}/images/all")
    public ResponseEntity<?> getAllImageBytesForGem(@PathVariable Long gemId) {
        try {
            log.info("Fetching all image bytes for gem id: {}", gemId);
            List<Map<String, Object>> images = gemService.getAllImageBytesForGem(gemId);
            log.info("Returning {} images for gem id: {}", images.size(), gemId);
            return ResponseEntity.ok(images);
        } catch (RuntimeException e) {
            log.warn("Error fetching images for gem {}: {}", gemId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    // ==================== PROTECTED ENDPOINTS ====================
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
            log.info("Reached upload controller....");
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