package com.team_inertia.gonly.repo;

import com.team_inertia.gonly.model.HiddenGem;
import com.team_inertia.gonly.model.Report;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByGemId(Long gemId);

    List<Report> findByReportedById(Long userId);

    @Modifying
    @Transactional
    void deleteByGem(HiddenGem gem);}