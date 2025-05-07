package com.moura.authorization.users.dtos;

import com.moura.authorization.users.validation.EmailConstraint;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;
import java.util.UUID;

public record RegistrationPostDTO(
        @NotBlank
        @EmailConstraint
        String email,

        @NotBlank
        String password,

        @NotBlank
        String name,

        @NotBlank
        String telefone,

        String description,

        Set<UUID> groupIds
) {}