package com.team_inertia.gonly.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@AllArgsConstructor
public class ReportRequest {

    private Long gemId;
    private String reason;
    private String description;

    public ReportRequest() {}
}