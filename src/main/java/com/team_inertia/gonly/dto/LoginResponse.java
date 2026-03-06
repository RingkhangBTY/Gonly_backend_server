package com.team_inertia.gonly.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {

    private String token;
    private String email;
    private String fullName;
    private Long userId;

    public LoginResponse() {}

    public LoginResponse(String token, String email, String fullName, Long userId) {
        this.token = token;
        this.email = email;
        this.fullName = fullName;
        this.userId = userId;
    }
}