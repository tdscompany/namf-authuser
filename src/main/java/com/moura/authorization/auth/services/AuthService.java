package com.moura.authorization.auth.services;

import com.moura.authorization.configs.TenantResolver;
import com.moura.authorization.context.TenantContext;
import com.moura.authorization.configs.security.providers.JwtProvider;
import com.moura.authorization.auth.dtos.AuthDto;
import com.moura.authorization.auth.dtos.TokenDto;
import com.moura.authorization.groups.repositories.PermissionRepository;
import com.moura.authorization.users.entities.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final TenantResolver tenantResolver;
    private final PermissionRepository permissionRepository;

    public AuthService(AuthenticationManager authenticationManager, JwtProvider jwtProvider, TenantResolver tenantResolver, PermissionRepository permissionRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.tenantResolver = tenantResolver;
        this.permissionRepository = permissionRepository;
    }

    public TokenDto authenticate(AuthDto authDto, UUID tenantIdHeader) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authDto.username(), authDto.password())
        );

        User user = (User) authentication.getPrincipal();
        UUID tenantId = tenantResolver.resolveTenant(user, tenantIdHeader);

        Authentication finalAuthentication = authentication;
        if (user.getOrganizationId() == null) {
            finalAuthentication = buildSuperadminAuthentication(user);
        }

        SecurityContextHolder.getContext().setAuthentication(finalAuthentication);
        TenantContext.setCurrentTenant(tenantId);

        String accessToken = jwtProvider.generateAccessToken(finalAuthentication, tenantId);
        String refreshToken = jwtProvider.generateRefreshToken(finalAuthentication);

        return new TokenDto(accessToken, refreshToken);
    }

    private Authentication buildSuperadminAuthentication(User user) {
        List<SimpleGrantedAuthority> allAuthorities = permissionRepository.findAll()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                .toList();

        return new UsernamePasswordAuthenticationToken(user, null, allAuthorities);
    }
}
