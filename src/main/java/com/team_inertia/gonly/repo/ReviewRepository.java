package com.team_inertia.gonly.repo;

import com.team_inertia.gonly.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Used by: ReviewController → GET /api/gems/{gemId}/reviews
    List<Review> findByGemId(Long gemId);

    // Used by: ReviewService → check existing review
    Optional<Review> findByGemIdAndUserId(Long gemId, Long userId);

    // Used by: ReviewService → prevent duplicate reviews
    boolean existsByGemIdAndUserId(Long gemId, Long userId);

    // Used by: ReviewService → calculate avg rating after review add/delete
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.gem.id = :gemId")
    Double findAverageRatingByGemId(@Param("gemId") Long gemId);

    // Used by: ReviewService → update review count on gem
    @Query("SELECT COUNT(r) FROM Review r WHERE r.gem.id = :gemId")
    Integer countByGemId(@Param("gemId") Long gemId);
}