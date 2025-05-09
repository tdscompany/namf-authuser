package com.moura.authorization.groups.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;
import java.util.UUID;

public record GroupRegistrationDTO (
       @NotBlank String name,
       @NotBlank String color,
       @NotEmpty Set<UUID> permissionIds
){}
