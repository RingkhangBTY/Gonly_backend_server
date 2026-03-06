package com.team_inertia.gonly.repo;

import com.team_inertia.gonly.model.GemImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GemImageRepository extends JpaRepository<GemImage, Long> {

    List<GemImage> findByGemId(Long gemId);
}