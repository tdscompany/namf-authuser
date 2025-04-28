package com.moura.authorization.auth.services;

import com.moura.authorization.auth.dtos.AuthDto;
import com.moura.authorization.auth.dtos.TokenDto;
import com.moura.authorization.configs.TenantResolver;
import com.moura.authorization.configs.security.providers.JwtProvider;
import com.moura.authorization.groups.entities.Permission;
import com.moura.authorization.groups.repositories.PermissionRepository;
import com.moura.authorization.users.entities.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private TenantResolver tenantResolver;

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private AuthService authService;


    @Test
    @DisplayName("should authenticate super admin and return token DTO with all permissions")
    void shouldAuthenticateSuperadminUserAndReturnTokenDto() {
        UUID tenantId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        AuthDto authDto = new AuthDto("username", "password");

        User mockUser = new User();
        mockUser.setId(UUID.randomUUID());
        mockUser.setOrganizationId(null);

        Authentication authentication = new UsernamePasswordAuthenticationToken(mockUser, null);

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(tenantResolver.resolveTenant(any(User.class), any()))
                .thenReturn(tenantId);

        Permission permission = new Permission();
        permission.setName("user:write");

        when(permissionRepository.findAll())
                .thenReturn(List.of(permission));

        when(jwtProvider.generateAccessToken(any(), any()))
                .thenReturn("access-token-superadmin");

        when(jwtProvider.generateRefreshToken(any()))
                .thenReturn("refresh-token-superadmin");

        TokenDto tokenDto = authService.authenticate(authDto, tenantId);

        assertNotNull(tokenDto);
        assertEquals("access-token-superadmin", tokenDto.getToken());
        assertEquals("refresh-token-superadmin", tokenDto.getRefreshToken());

        Authentication contextAuth = SecurityContextHolder.getContext().getAuthentication();
        assertTrue(contextAuth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("user:write")));

        verify(authenticationManager, times(1)).authenticate(any());
        verify(jwtProvider, times(1)).generateAccessToken(any(), eq(tenantId));
        verify(jwtProvider, times(1)).generateRefreshToken(any());
        verify(permissionRepository, times(1)).findAll();
        verify(tenantResolver, times(1)).resolveTenant(any(User.class), eq(tenantId));
    }

    @Test
    @DisplayName("Should authenticate user and return token dto")
    void shouldAuthenticateUserAndReturnTokenDto() {
        AuthDto authDto = new AuthDto("username", "password");

        User mockUser = new User();
        mockUser.setId(UUID.randomUUID());
        mockUser.setOrganizationId(UUID.randomUUID());

        Authentication authentication = new UsernamePasswordAuthenticationToken(mockUser, null);

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(tenantResolver.resolveTenant(any(User.class), any()))
                .thenReturn(UUID.randomUUID());

        when(jwtProvider.generateAccessToken(any(), any()))
                .thenReturn("access-token");

        when(jwtProvider.generateRefreshToken(any()))
                .thenReturn("refresh-token");

        TokenDto tokenDto = authService.authenticate(authDto, UUID.randomUUID());

        assertNotNull(tokenDto);
        assertEquals("access-token", tokenDto.getToken());
        assertEquals("refresh-token", tokenDto.getRefreshToken());

        verify(authenticationManager, times(1)).authenticate(any());
        verify(jwtProvider, times(1)).generateAccessToken(any(), any());
        verify(jwtProvider, times(1)).generateRefreshToken(any());
    }

    @Test
    @DisplayName("should throw exception when authentication fails")
    void shouldThrowExceptionWhenAuthenticationFails() {
        AuthDto authDto = new AuthDto("username", "wrongpassword");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> {
            authService.authenticate(authDto, UUID.randomUUID());
        });
    }
}
