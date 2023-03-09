package com.example.rest_api_jwt.Controllers.auth;

import com.example.rest_api_jwt.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class AuthenticationResponse {
    private String token;
    private User user;

    public AuthenticationResponse(String token, User user) {
        this.token = token;
        this.user = user;
    }





}
