package com.moura.authorization.auth.dtos;

import lombok.Data;

import java.io.Serializable;

@Data
public class TokenDto {
    private String refreshToken;
    private String token;

    private String type = "Bearer";

    public TokenDto(String accessToken, String refreshToken) {
        this.token = accessToken;
        this.refreshToken = refreshToken;
    }
}
