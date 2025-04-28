package com.moura.authorization.auth.dtos;

import jakarta.validation.constraints.NotBlank;

public record AuthDto(
        @NotBlank String username,
        @NotBlank String password
) { }
