package com.moura.authorization.groups.event;

import lombok.Builder;

import java.util.UUID;

@Builder
public record GroupCreatedPayload(UUID groupId) {}
