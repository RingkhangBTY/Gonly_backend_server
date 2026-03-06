package com.team_inertia.gonly.repo;

import com.team_inertia.gonly.enums.GemStatus;
import com.team_inertia.gonly.model.LocalEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LocalEventRepository extends JpaRepository<LocalEvent, Long> {

    List<LocalEvent> findByStatus(GemStatus status);

    List<LocalEvent> findByStatusAndStateIgnoreCase(GemStatus status, String state);

    List<LocalEvent> findByStatusAndEndDateGreaterThanEqual(GemStatus status, LocalDate date);

    List<LocalEvent> findBySubmittedById(Long userId);
}