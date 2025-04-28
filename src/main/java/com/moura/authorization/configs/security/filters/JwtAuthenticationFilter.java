package com.moura.authorization.configs.security.filters;

import com.moura.authorization.auth.entities.SecurityAuthority;
import com.moura.authorization.auth.services.UserDetailsImpl;
import com.moura.authorization.configs.security.TenantContext;
import com.moura.authorization.configs.security.providers.JwtProvider;
import com.moura.authorization.exceptions.JwtAuthenticationException;
import com.moura.authorization.groups.entities.Permission;
import com.moura.authorization.groups.repositories.PermissionRepository;
import com.moura.authorization.users.entities.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    private final UserDetailsImpl userDetailsService;

    private final PermissionRepository permissionRepository;

    public JwtAuthenticationFilter(JwtProvider jwtProvider,
                                   UserDetailsImpl userDetailsService, PermissionRepository permissionRepository) {
        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
        this.permissionRepository = permissionRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = extractToken(request);

            if (token != null && jwtProvider.validateJwt(token)) {
                processAuthentication(token);
            }

            filterChain.doFilter(request, response);

        } finally {
            TenantContext.clear();
        }
    }

    private String extractToken(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }

    private void processAuthentication(String token) {
        String userId = jwtProvider.getSubjectFromJwt(token);
        UUID tenantId = jwtProvider.getTenantIdFromJwt(token);

        TenantContext.setCurrentTenant(tenantId);

        User user = (User) userDetailsService.loadUserByUserId(UUID.fromString(userId));

        List<GrantedAuthority> authorities = loadAuthoritiesForUser(user);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user, null, authorities
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private List<GrantedAuthority> loadAuthoritiesForUser(User user) {
        if (user.getOrganizationId() == null) {
            return permissionRepository.findAll()
                    .stream()
                    .map(SecurityAuthority::new)
                    .collect(Collectors.toList());
        } else {
            return user.getAuthorities()
                    .stream()
                    .map(authority -> new SecurityAuthority((Permission) authority))
                    .collect(Collectors.toList());
        }
    }
}
