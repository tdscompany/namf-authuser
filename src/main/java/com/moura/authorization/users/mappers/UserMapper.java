package com.moura.authorization.users.mappers;

import com.moura.authorization.groups.entities.Group;
import com.moura.authorization.groups.mappers.GroupIdMapper;
import com.moura.authorization.users.dtos.UserDTO;
import com.moura.authorization.users.entities.Credentials;
import com.moura.authorization.users.entities.User;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, uses = { GroupIdMapper.class })
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "groups", source = "groupIds")
    @Mapping(target = "userStatus", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "credentials", ignore = true)
    @Mapping(target = "organizationId", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "passwordNotEncoded", ignore = true)
    @Mapping(target = "version", ignore = true)
    User toEntity(UserDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "groups", source = "groupIds")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "credentials", ignore = true)
    @Mapping(target = "organizationId", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "passwordNotEncoded", ignore = true)
    @Mapping(target = "version", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(UserDTO dto, @MappingTarget User entity);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "groups", source = "groups")
    @Mapping(target = "oldPassword", ignore = true)
    @Mapping(target = "groupIds", ignore = true)
    UserDTO toDTO(User user);

    default Page<UserDTO> toDTOPage(Page<User> users) {
        return users.map(this::toDTO);
    }

}