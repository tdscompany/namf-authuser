package com.moura.authorization.groups.event;

import lombok.Builder;

import java.util.UUID;

@Builder
public record GroupDeletedPayload(UUID groupId) {
}
