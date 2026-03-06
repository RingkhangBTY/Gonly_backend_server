package com.team_inertia.gonly.repo;

import com.team_inertia.gonly.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByGemId(Long gemId);

    List<Report> findByReportedById(Long userId);
}