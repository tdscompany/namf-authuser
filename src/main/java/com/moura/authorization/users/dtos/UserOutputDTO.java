package com.moura.authorization.users.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.moura.authorization.groups.dtos.GroupDTO;
import com.moura.authorization.users.enums.UserStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserOutputDTO(
        UUID id,
        String email,
        String name,
        String telefone,
        String description,
        UserStatus userStatus,
        Set<GroupDTO> groups,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
        LocalDateTime createdAt,
        LocalDateTime lastAccess
) {}
