package com.moura.authorization.auth.event;

import lombok.Builder;

import java.util.UUID;

@Builder
public record AuthSuccessPayload(UUID userId, String email) {}
