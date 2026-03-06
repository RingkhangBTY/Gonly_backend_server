package com.team_inertia.gonly.dto;

import com.team_inertia.gonly.model.Review;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class ReviewResponse {

    private Long id;
    private Long gemId;
    private Long userId;
    private String userName;
    private Integer rating;
    private String comment;
    private Integer visitMonth;
    private LocalDateTime createdAt;
    private List<Long> imageIds;

    public ReviewResponse() {}

    public static ReviewResponse fromEntity(Review review) {
        ReviewResponse r = new ReviewResponse();
        r.setId(review.getId());
        r.setGemId(review.getGem().getId());
        r.setUserId(review.getUser().getId());
        r.setUserName(review.getUser().getFullName());
        r.setRating(review.getRating());
        r.setComment(review.getComment());
        r.setVisitMonth(review.getVisitMonth());
        r.setCreatedAt(review.getCreatedAt());

        if (review.getImages() != null) {
            r.setImageIds(
                    review.getImages().stream()
                            .map(img -> img.getId())
                            .collect(Collectors.toList())
            );
        }
        return r;
    }
}