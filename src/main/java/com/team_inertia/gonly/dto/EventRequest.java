package com.team_inertia.gonly.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
public class EventRequest {

    private String title;
    private String description;
    private Double latitude;
    private Double longitude;
    private String locationSource;
    private String state;
    private String eventType;
    private String startDate;
    private String endDate;

}