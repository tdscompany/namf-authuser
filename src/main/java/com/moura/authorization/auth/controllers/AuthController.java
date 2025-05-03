package com.moura.authorization.auth.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.moura.authorization.auth.dtos.AuthDto;
import com.moura.authorization.auth.dtos.TokenDto;
import com.moura.authorization.auth.services.AuthService;
import com.moura.authorization.users.dtos.UserDTO;
import com.moura.authorization.users.entities.User;
import com.moura.authorization.users.mappers.UserMapper;
import com.moura.authorization.users.services.UserService;
import com.moura.authorization.utils.MessageUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping()
    public ResponseEntity<TokenDto> auth(
            @RequestBody AuthDto authDto,
            @RequestHeader(value = "X-Tenant-ID", required = false) UUID tenantId
    ) {
        TokenDto token = authService.authenticate(authDto, tenantId);
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }
}
