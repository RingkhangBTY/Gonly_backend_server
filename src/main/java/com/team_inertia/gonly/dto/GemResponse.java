package com.team_inertia.gonly.dto;

import com.team_inertia.gonly.model.HiddenGem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@AllArgsConstructor
public class GemResponse {

    private Long id;
    private String name;
    private String description;
    private Double latitude;
    private Double longitude;
    private String locationSource;
    private String category;
    private String state;
    private String nearestTown;
    private String addressAuto;
    private String travelTips;
    private String howToReach;
    private String entryFee;
    private Integer bestSeasonStart;
    private Integer bestSeasonEnd;
    private String seasonWarning;
    private String difficultyLevel;
    private String safetyNote;
    private String localContact;
    private Boolean networkAvailable;
    private BigDecimal avgRating;
    private Integer reviewCount;
    private Integer viewCount;
    private String status;
    private String submittedByName;
    private Long submittedById;
    private LocalDateTime createdAt;
    private List<Long> imageIds;

    public GemResponse() {}

    public static GemResponse fromEntity(HiddenGem gem) {
        GemResponse r = new GemResponse();
        r.setId(gem.getId());
        r.setName(gem.getName());
        r.setDescription(gem.getDescription());
        r.setLatitude(gem.getLatitude().doubleValue());
        r.setLongitude(gem.getLongitude().doubleValue());
        r.setLocationSource(gem.getLocationSource().name());
        r.setCategory(gem.getCategory().name());
        r.setState(gem.getState());
        r.setNearestTown(gem.getNearestTown());
        r.setAddressAuto(gem.getAddressAuto());
        r.setTravelTips(gem.getTravelTips());
        r.setHowToReach(gem.getHowToReach());
        r.setEntryFee(gem.getEntryFee());
        r.setBestSeasonStart(gem.getBestSeasonStart());
        r.setBestSeasonEnd(gem.getBestSeasonEnd());
        r.setSeasonWarning(gem.getSeasonWarning());
        r.setDifficultyLevel(gem.getDifficultyLevel().name());
        r.setSafetyNote(gem.getSafetyNote());
        r.setLocalContact(gem.getLocalContact());
        r.setNetworkAvailable(gem.getNetworkAvailable());
        r.setAvgRating(gem.getAvgRating());
        r.setReviewCount(gem.getReviewCount());
        r.setViewCount(gem.getViewCount());
        r.setStatus(gem.getStatus().name());
        r.setSubmittedByName(gem.getSubmittedBy().getFullName());
        r.setSubmittedById(gem.getSubmittedBy().getId());
        r.setCreatedAt(gem.getCreatedAt());

        if (gem.getImages() != null) {
            r.setImageIds(
                    gem.getImages().stream()
                            .map(img -> img.getId())
                            .collect(Collectors.toList())
            );
        }
        return r;
    }
}