package com.team_inertia.gonly.controller;

import com.team_inertia.gonly.dto.*;
import com.team_inertia.gonly.model.EventImage;
import com.team_inertia.gonly.model.User;
import com.team_inertia.gonly.service.AuthService;
import com.team_inertia.gonly.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;
    private final AuthService authService;

    public EventController(EventService eventService, AuthService authService) {
        this.eventService = eventService;
        this.authService = authService;
    }

    // GET /api/events — PUBLIC
    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    // GET /api/events/upcoming — PUBLIC
    @GetMapping("/upcoming")
    public ResponseEntity<List<EventResponse>> getUpcomingEvents() {
        return ResponseEntity.ok(eventService.getUpcomingEvents());
    }

    // GET /api/events/state?name=Nagaland — PUBLIC
    @GetMapping("/state")
    public ResponseEntity<List<EventResponse>> getEventsByState(@RequestParam String name) {
        return ResponseEntity.ok(eventService.getEventsByState(name));
    }

    // GET /api/events/{id} — PUBLIC
    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(eventService.getEventById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // GET /api/events/images/{imageId} — PUBLIC
    @GetMapping("/images/{imageId}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long imageId) {
        try {
            EventImage image = eventService.getImageById(imageId);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, image.getImageType())
                    .body(image.getImageData());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /api/events — PROTECTED
    @PostMapping
    public ResponseEntity<?> createEvent(
            @RequestBody EventRequest request,
            Authentication authentication) {
        try {
            User user = authService.getUserByEmail(authentication.getName());
            EventResponse response = eventService.createEvent(request, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // POST /api/events/{id}/images — PROTECTED
    @PostMapping("/{id}/images")
    public ResponseEntity<?> uploadImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile file,
            Authentication authentication) {
        try {
            User user = authService.getUserByEmail(authentication.getName());
            eventService.addImageToEvent(id, file, user);
            return ResponseEntity.ok(new ApiResponse(true, "Image uploaded successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
}