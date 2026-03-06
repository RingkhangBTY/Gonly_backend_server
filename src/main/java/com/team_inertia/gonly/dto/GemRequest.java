package com.team_inertia.gonly.dto;

import jakarta.websocket.server.ServerEndpoint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GemRequest {

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

    public GemRequest() {}
}