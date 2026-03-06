package com.team_inertia.gonly.service;

import com.team_inertia.gonly.dto.ReviewRequest;
import com.team_inertia.gonly.dto.ReviewResponse;
import com.team_inertia.gonly.model.HiddenGem;
import com.team_inertia.gonly.model.Review;
import com.team_inertia.gonly.model.User;
import com.team_inertia.gonly.repo.HiddenGemRepository;
import com.team_inertia.gonly.repo.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private HiddenGemRepository gemRepository;

    public ReviewResponse addReview(Long gemId, ReviewRequest request, User user) {
        if (reviewRepository.existsByGemIdAndUserId(gemId, user.getId())) {
            throw new RuntimeException("You have already reviewed this gem");
        }
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        HiddenGem gem = gemRepository.findById(gemId)
                .orElseThrow(() -> new RuntimeException("Gem not found with id: " + gemId));

        Review review = new Review();
        review.setGem(gem);
        review.setUser(user);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setVisitMonth(request.getVisitMonth());

        Review savedReview = reviewRepository.save(review);
        updateGemRating(gemId);
        return ReviewResponse.fromEntity(savedReview);
    }

    public List<ReviewResponse> getReviewsByGemId(Long gemId) {
        return reviewRepository.findByGemId(gemId)
                .stream().map(ReviewResponse::fromEntity).collect(Collectors.toList());
    }

    public void deleteReview(Long reviewId, User user) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + reviewId));
        if (!review.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only delete your own reviews");
        }
        Long gemId = review.getGem().getId();
        reviewRepository.delete(review);
        updateGemRating(gemId);
    }

    private void updateGemRating(Long gemId) {
        HiddenGem gem = gemRepository.findById(gemId)
                .orElseThrow(() -> new RuntimeException("Gem not found"));
        Double avgRating = reviewRepository.findAverageRatingByGemId(gemId);
        Integer count = reviewRepository.countByGemId(gemId);
        gem.setAvgRating(avgRating != null ? BigDecimal.valueOf(avgRating) : BigDecimal.ZERO);
        gem.setReviewCount(count != null ? count : 0);
        gemRepository.save(gem);
    }
}