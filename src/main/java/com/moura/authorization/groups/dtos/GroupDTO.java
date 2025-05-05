package com.moura.authorization.groups.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class GroupDTO {

    private UUID id;
    private String name;

}
