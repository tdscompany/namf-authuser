package com.moura.authorization.users.mappers;

import com.moura.authorization.groups.entities.Group;
import com.moura.authorization.groups.repositories.GroupRepository;
import com.moura.authorization.groups.repositories.specification.GroupSpecification;
import com.moura.authorization.users.dtos.UserDTO;
import com.moura.authorization.users.dtos.UserFilterDTO;
import com.moura.authorization.users.entities.User;
import com.moura.authorization.users.repositories.specification.UserSpecification;
import org.modelmapper.Conditions;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
public class UserMapper {

    private final ModelMapper modelMapper;
    private final GroupRepository groupRepository;

    public UserMapper(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
        this.modelMapper = new ModelMapper();
        configureMappings();
    }

    private void configureMappings() {
        Converter<Set<UUID>, Set<Group>> groupIdToGroupConverter = ctx -> {
            Set<UUID> groupIds = ctx.getSource();
            if (groupIds == null || groupIds.isEmpty()) return Collections.emptySet();
            return new HashSet<>(groupRepository.findAll(GroupSpecification.idIn(groupIds)));
        };

        modelMapper.typeMap(UserDTO.class, User.class)
                .addMappings(mapper -> {
                    mapper.skip(User::setId);
                    mapper.using(groupIdToGroupConverter).map(UserDTO::getGroupIds, User::setGroups);
                    mapper.skip(User::setUserStatus);
                    mapper.skip(User::setCreatedBy);
                    mapper.skip(User::setUpdatedBy);
                    mapper.skip(User::setCreatedAt);
                    mapper.skip(User::setUpdatedAt);
                    mapper.skip(User::setCredentials);
                    mapper.skip(User::setOrganizationId);
                    mapper.skip(User::setPasswordNotEncoded);
                    mapper.skip(User::setVersion);
                });

        modelMapper.typeMap(User.class, UserDTO.class)
                .addMappings(mapper -> {
                    mapper.skip(UserDTO::setPassword);
                    mapper.skip(UserDTO::setOldPassword);
                    mapper.skip(UserDTO::setGroupIds);
                });
    }

    public User toEntity(UserDTO dto) {
        return modelMapper.map(dto, User.class);
    }

    public void updateEntityFromDTO(UserDTO dto, User entity) {
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        modelMapper.map(dto, entity);
    }

    public UserDTO toDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    public Page<UserDTO> toDTOPage(Page<User> users) {
        return users.map(this::toDTO);
    }
}