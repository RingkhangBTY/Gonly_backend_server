package com.team_inertia.gonly.service;

import com.team_inertia.gonly.dto.ReportRequest;
import com.team_inertia.gonly.enums.ReportReason;
import com.team_inertia.gonly.model.HiddenGem;
import com.team_inertia.gonly.model.Report;
import com.team_inertia.gonly.model.User;
import com.team_inertia.gonly.repo.HiddenGemRepository;
import com.team_inertia.gonly.repo.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private HiddenGemRepository gemRepository;

    public Report createReport(ReportRequest request, User user) {
        HiddenGem gem = gemRepository.findById(request.getGemId())
                .orElseThrow(() -> new RuntimeException("Gem not found with id: " + request.getGemId()));

        Report report = new Report();
        report.setGem(gem);
        report.setReportedBy(user);
        report.setReason(ReportReason.valueOf(request.getReason()));
        report.setDescription(request.getDescription());

        return reportRepository.save(report);
    }
}