package com.team_inertia.gonly.repo;

import com.team_inertia.gonly.model.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    List<Bookmark> findByUserId(Long userId);

    Optional<Bookmark> findByUserIdAndGemId(Long userId, Long gemId);

    boolean existsByUserIdAndGemId(Long userId, Long gemId);
}