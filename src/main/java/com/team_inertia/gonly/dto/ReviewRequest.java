package com.team_inertia.gonly.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequest {

    private Integer rating;
    private String comment;
    private Integer visitMonth;

    public ReviewRequest() {}
}