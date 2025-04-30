package com.moura.authorization.users.mappers;

import com.moura.authorization.groups.entities.Group;
import com.moura.authorization.groups.mappers.GroupIdMapper;
import com.moura.authorization.users.dtos.UserDTO;
import com.moura.authorization.users.entities.Credentials;
import com.moura.authorization.users.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Mapping(target = "groupIds", source = "groups", qualifiedByName = "mapGroupsToIds")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "oldPassword", ignore = true)
    UserDTO toDTO(User user);

    @Named("mapGroupsToIds")
    default Set<UUID> mapGroupsToIds(Set<Group> groups) {
        return groups.stream().map(Group::getId).collect(Collectors.toSet());
    }
}