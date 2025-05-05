package com.moura.authorization.groups.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PermissionDTO {
    private UUID id;
    private String permission;
}
