package com.moura.authorization.auth.controllers;

import com.moura.authorization.auth.services.AuthService;
import com.moura.authorization.configs.security.managers.CustomAuthenticationManager;
import com.moura.authorization.configs.security.providers.JwtProvider;
import com.moura.authorization.dtos.AuthDto;
import com.moura.authorization.dtos.TokenDto;
import com.nimbusds.jose.JOSEException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {


    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PreAuthorize("hasAuthority('user:write')")
    @GetMapping("/demo")
    public String demo() {
        var u = SecurityContextHolder.getContext().getAuthentication();
        return "Hello, this is a demo endpoint!";
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
