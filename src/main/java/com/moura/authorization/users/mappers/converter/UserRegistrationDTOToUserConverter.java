package com.moura.authorization.users.mappers.converter;

import com.moura.authorization.groups.entities.Group;
import com.moura.authorization.groups.services.GroupService;
import com.moura.authorization.users.dtos.UserRegistrationDTO;
import com.moura.authorization.users.entities.User;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
public class UserRegistrationDTOToUserConverter implements Converter<UserRegistrationDTO, User> {

    private final GroupService groupService;

    public UserRegistrationDTOToUserConverter(GroupService groupService) {
        this.groupService = groupService;
    }

    private Set<Group> fetchGroups(Set<UUID> groupIds) {
        return groupService.validateGroupIds(groupIds);
    }

    @Override
    public User convert(MappingContext<UserRegistrationDTO, User> context) {
        UserRegistrationDTO dto = context.getSource();
        User user = new User();
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setTelefone(dto.telefone());
        user.setDescription(dto.description());
        user.setPasswordNotEncoded(dto.password());
        user.setGroups(fetchGroups(dto.groupIds()));
        return user;
    }
}
