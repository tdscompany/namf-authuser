package com.moura.authorization.configs.security.filters;

import com.moura.authorization.auth.services.UserDetailsImpl;
import com.moura.authorization.configs.security.TenantContext;
import com.moura.authorization.configs.security.providers.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    private final UserDetailsImpl userDetailsService;

    public JwtAuthenticationFilter(JwtProvider jwtProvider,
                                   UserDetailsImpl userDetailsService) {
        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String jwtStr = getTokenHeader(request);

        if (jwtStr != null && jwtProvider.validateJwt(jwtStr)) {
            String userId = jwtProvider.getSubjectFromJwt(jwtStr);
            UUID tenantId = jwtProvider.getTenantIdFromJwt(jwtStr);

            TenantContext.setCurrentTenant(tenantId);

            UserDetails userDetails = userDetailsService.loadUserByUserId(UUID.fromString(userId));

            if (userDetails != null) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        TenantContext.clear();
        filterChain.doFilter(request, response);
    }

    private String getTokenHeader(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
