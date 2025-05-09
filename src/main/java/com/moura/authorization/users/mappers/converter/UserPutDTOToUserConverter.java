package com.moura.authorization.users.mappers.converter;

import com.moura.authorization.groups.entities.Group;
import com.moura.authorization.groups.services.GroupService;
import com.moura.authorization.users.dtos.UserPutDTO;
import com.moura.authorization.users.entities.User;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
public class UserPutDTOToUserConverter implements Converter<UserPutDTO, User> {

    private final GroupService groupService;

    public UserPutDTOToUserConverter(GroupService groupService) {
        this.groupService = groupService;
    }


    @Override
    public User convert(MappingContext<UserPutDTO, User> context) {
        UserPutDTO dto = context.getSource();
        User user = context.getDestination();
        if (dto.name() != null) user.setName(dto.name());
        if (dto.telefone() != null) user.setTelefone(dto.telefone());
        if (dto.description() != null) user.setDescription(dto.description());
        if (dto.email() != null) user.setEmail(dto.email());
        if (dto.userStatus() != null) user.setUserStatus(dto.userStatus());
        user.setGroups(fetchGroups(dto.groupIds()));
        return user;
    }

    private Set<Group> fetchGroups(Set<UUID> groupIds) {
        return groupService.validateGroupIds(groupIds);
    }

}

