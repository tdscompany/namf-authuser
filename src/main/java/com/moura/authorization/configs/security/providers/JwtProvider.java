package com.moura.authorization.configs.security.providers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.moura.authorization.users.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.Period;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtProvider {
    @Value("${jwt.key.value}")
    private String jwtSecret;

    @Value("${jwt.expiration.access}")
    private int accessTokenExpirationMs;

    @Value("${jwt.expiration.refresh}")
    private int refreshTokenExpirationDays;

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(jwtSecret);
    }

    public String generateAccessToken(Authentication authentication, UUID tenantId) {
        User user = (User) authentication.getPrincipal();
        Instant now = Instant.now();

        return JWT.create()
                .withSubject(user.getId().toString())
                .withClaim("roles", authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .withClaim("org_id", tenantId.toString())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plusMillis(accessTokenExpirationMs)))
                .sign(getAlgorithm());
    }

    public String generateRefreshToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Instant now = Instant.now();

        return JWT.create()
                .withSubject(user.getId().toString())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plus(Period.ofDays(refreshTokenExpirationDays))))
                .sign(getAlgorithm());
    }

    public boolean validateJwt(String token) {
        try {
            JWT.require(getAlgorithm()).build().verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    public String getSubjectFromJwt(String token) {
        return JWT.require(getAlgorithm()).build().verify(token).getSubject();
    }

    public UUID getTenantIdFromJwt(String token) {
        String orgId = JWT.require(getAlgorithm()).build().verify(token).getClaim("org_id").asString();
        return UUID.fromString(orgId);
    }
}
