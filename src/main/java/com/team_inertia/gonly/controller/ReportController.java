package com.team_inertia.gonly.controller;

import com.team_inertia.gonly.dto.*;
import com.team_inertia.gonly.model.User;
import com.team_inertia.gonly.service.AuthService;
import com.team_inertia.gonly.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@Slf4j
public class ReportController {

    private ReportService reportService;
    private AuthService authService;

    public ReportController(ReportService reportService, AuthService authService) {
        this.reportService = reportService;
        this.authService = authService;
    }

    // POST /api/reports — PROTECTED
    @PostMapping
    public ResponseEntity<?> createReport(
            @RequestBody ReportRequest request,
            Authentication authentication) {
        log.info("Requested for report submission..");
        try {
            User user = authService.getUserByEmail(authentication.getName());
            reportService.createReport(request, user);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Report submitted successfully"));
        } catch (RuntimeException e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
}