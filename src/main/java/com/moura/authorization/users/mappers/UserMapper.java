package com.moura.authorization.users.mappers;

import com.moura.authorization.users.dtos.UserDTO;
import com.moura.authorization.users.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "credentials", ignore = true)
    @Mapping(target = "groups", ignore = true)
    @Mapping(target = "userStatus", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(UserDTO dto);
}