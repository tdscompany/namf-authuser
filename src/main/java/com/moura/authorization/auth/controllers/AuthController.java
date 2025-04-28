package com.moura.authorization.auth.controllers;

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

    final private JwtProvider jwtProvider;

    final private AuthenticationManager customAuthenticationManager;

    public AuthController(JwtProvider jwtProvider, AuthenticationManager customAuthenticationManager) {
        this.jwtProvider = jwtProvider;
        this.customAuthenticationManager = customAuthenticationManager;
    }


    @PreAuthorize("hasAuthority('user:write')")
    @GetMapping("/demo")
    public String demo() {
        var u = SecurityContextHolder.getContext().getAuthentication();
        u.getAuthorities().forEach(System.out::println);
        return "Hello, this is a demo endpoint!";
    }

    @PostMapping()
    public ResponseEntity<TokenDto> auth(
            @RequestBody AuthDto authDto,
            @RequestHeader(value = "X-Tenant-ID", required = false) UUID tenantId
    ) {

        Authentication authentication = customAuthenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authDto.username(), authDto.password())
        );
        
        if (authentication.isAuthenticated()) SecurityContextHolder.getContext().setAuthentication(authentication);

        var accessToken = jwtProvider.generateAccessToken(authentication,tenantId);
        var refreshToken = jwtProvider.generateRefreshToken(authentication);

        return ResponseEntity.status(HttpStatus.OK).body(new TokenDto(accessToken, refreshToken));
    }
}
