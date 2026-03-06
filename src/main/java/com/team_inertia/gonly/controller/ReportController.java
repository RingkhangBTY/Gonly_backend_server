package com.team_inertia.gonly.controller;

import com.team_inertia.gonly.dto.*;
import com.team_inertia.gonly.model.User;
import com.team_inertia.gonly.service.AuthService;
import com.team_inertia.gonly.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private AuthService authService;

    // POST /api/reports — PROTECTED
    @PostMapping
    public ResponseEntity<?> createReport(
            @RequestBody ReportRequest request,
            Authentication authentication) {
        try {
            User user = authService.getUserByEmail(authentication.getName());
            reportService.createReport(request, user);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Report submitted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
}