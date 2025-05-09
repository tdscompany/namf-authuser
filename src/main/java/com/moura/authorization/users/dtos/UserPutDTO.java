package com.moura.authorization.users.dtos;

import com.moura.authorization.users.enums.UserStatus;

import java.util.Set;
import java.util.UUID;

public record UserPutDTO(
        String name,
        String telefone,
        String description,
        String email,
        Set<UUID> groupIds,
        UserStatus userStatus
) {}
