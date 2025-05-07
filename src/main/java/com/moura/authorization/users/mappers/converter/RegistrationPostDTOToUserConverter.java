package com.moura.authorization.users.mappers.converter;

import com.moura.authorization.groups.entities.Group;
import com.moura.authorization.groups.repositories.GroupRepository;
import com.moura.authorization.users.dtos.RegistrationPostDTO;
import com.moura.authorization.users.entities.User;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
public class RegistrationPostDTOToUserConverter implements Converter<RegistrationPostDTO, User> {

    private final GroupRepository groupRepository;

    public RegistrationPostDTOToUserConverter(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    private Set<Group> fetchGroups(Set<UUID> groupIds) {
        return (CollectionUtils.isEmpty(groupIds))
                ? Set.of()
                : new HashSet<>(groupRepository.findAllById(groupIds));
    }

    @Override
    public User convert(MappingContext<RegistrationPostDTO, User> context) {
        RegistrationPostDTO dto = context.getSource();
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
