package com.moura.authorization.groups.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class GroupDTO {

    private UUID id;
    private String name;

}
