package com.moura.authorization.dtos;

import lombok.Data;

import java.io.Serializable;

@Data
public class TokenDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String token;
    private String refreshToken;
    private String type = "Bearer";

    public TokenDto(String accessToken, String refreshToken) {
        this.token = accessToken;
        this.refreshToken = refreshToken;
    }
}
