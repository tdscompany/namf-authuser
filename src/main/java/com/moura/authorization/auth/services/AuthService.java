package com.moura.authorization.auth.services;

import com.moura.authorization.auth.dtos.AuthDto;
import com.moura.authorization.auth.dtos.TokenDto;

import java.util.UUID;

public interface AuthService {
    TokenDto authenticate(AuthDto authDto, UUID tenantIdHeader);
}
